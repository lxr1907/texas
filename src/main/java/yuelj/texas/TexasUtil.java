package yuelj.texas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.websocket.Session;

import yuelj.action.websocket.TexasWS;
import yuelj.constants.RoomTypeList;
import yuelj.entity.Player;
import yuelj.entity.RetMsg;
import yuelj.service.PlayerService;
import yuelj.texas.robot.RobotManager;
import yuelj.texas.threeCard.ThreeCardRoom;
import yuelj.utils.SpringUtil;
import yuelj.utils.logs.SystemLog;
import yuelj.utils.serialize.JsonUtils;

public class TexasUtil {

	/**
	 * 获取一个对应级别的可用房间，没有则新建一个
	 * 
	 * @param level
	 * @return
	 */
	public static Room getUsableRoom(int level, Player p) {
		Room usableroom = null;
		Room roomConfig = RoomTypeList.roomTypeMap.get(level);
		if (p.getChips() < roomConfig.getMinChips()) {
			return null;
		}
		for (int i = 0; i < TexasStatic.roomList.size(); i++) {
			int roomstate = TexasStatic.roomList.get(i).getRoomstate();
			if (roomstate == 1 && TexasStatic.roomList.get(i).getLevel() == level) {
				usableroom = TexasStatic.roomList.get(i);
				break;
			}
		}
		if (usableroom == null) {
			usableroom = createRoom(level);
		}
		return usableroom;
	}

	/**
	 * 获取可用房间类型
	 * 
	 * @param level
	 * @param p
	 * @param type  1宝鸡拼三张，不传默认德州扑克
	 * @return
	 */
	public static Room getUsableRoom(int level, Player p, int type) {
		// 加入游戏类型，1宝鸡拼三张，不传默认德州扑克
		if (type == 1) {
			ThreeCardRoom usableroom = null;
			ThreeCardRoom roomConfig = RoomTypeList.threeCardRoomTypeMap.get(level);
			if (p.getChips() < roomConfig.getMinChips()) {
				return null;
			}
			for (int i = 0; i < TexasStatic.threeCardRoomList.size(); i++) {
				int roomstate = TexasStatic.threeCardRoomList.get(i).getRoomstate();
				if (roomstate == 1 && TexasStatic.threeCardRoomList.get(i).getLevel() == level) {
					usableroom = TexasStatic.threeCardRoomList.get(i);
					break;
				}
			}
			if (usableroom == null) {
				usableroom = createThreeCardRoomRoom(level);
			}
			return usableroom;
		} else {
			return getUsableRoom(level, p);
		}
	}

	/**
	 * 创建一个新房间，将玩家放入房间，然后再将房间放入大厅
	 * 
	 * @param level
	 * @param player
	 */
	public static Room createRoomThenIn(int level, Player player) {
		Room newRoom = createRoom(level);
		inRoom(newRoom, player);
		TexasStatic.roomList.add(newRoom);
		return newRoom;
	}

	public static Room createRoomThenIn(int level, Player player, int type) {
		if (type == 1) {
			ThreeCardRoom newRoom = createThreeCardRoomRoom(level);
			inRoom(newRoom, player);
			TexasStatic.threeCardRoomList.add(newRoom);
			return newRoom;
		} else {
			Room newRoom = createRoom(level);
			inRoom(newRoom, player);
			TexasStatic.roomList.add(newRoom);
			return newRoom;
		}
	}

	/**
	 * 进入房间
	 * 
	 * <pre>
	 * 1，检查房间是否还可加入 2，加入房间/重新查找可以进入的房间 3，改变房间状态
	 */
	public static boolean inRoom(Room room, Player player) {
		if (room == null) {
			return false;
		}
		// 房间加锁
		synchronized (room.getFreeSeatStack()) {
			// 房间满人，修改状态为不可加入
			if (room.getFreeSeatStack().isEmpty()) {
				room.setRoomstate(0);
				return false;// 加入房间失败
			}
			room.getWaitPlayers().add(player);
			// 设定座位号
			if (player.getSeatNum() == -1) {
				int seatNum = room.getFreeSeatStack().pop();// 从空闲座位的栈中取出一个座位
				player.setSeatNum(seatNum);
			}
			room.assignChipsForInRoom(player);

			// 成功则设置房间
			player.setRoom(room);
		}
		return true;
	}

	/**
	 * 退出房间
	 */
	public static void outRoom(Player player) {
		if (player == null || player.getRoom() == null) {
			return;
		}
		synchronized (player.getRoom().getFreeSeatStack()) {
			Room room = player.getRoom();
			// 通知所有房间内玩家，有玩家离开
			sendPlayerToOthers(player, room, "onPlayerLeaveRoom");
			removeWaitOrInGamePlayer(player);
			// 成功则设置房间
			int index = room.donePlayerList.indexOf(player.getSeatNum());
			if (index != -1) {
				room.donePlayerList.remove(index);
			}
			// 记录玩家的筹码变化
			room.assignChipsForOutRoom(player);
			// 还座位号
			if (player.getSeatNum() != -1) {
				room.getFreeSeatStack().push(player.getSeatNum());
				player.setSeatNum(-1);
			}
			// 修改房间状态为可加入
			room.setRoomstate(1);
			// 在游戏中的玩家数少于最低玩家数时结束游戏
			room.checkEnd();
		}
	}

	/**
	 * 移除等待或游戏中的玩家
	 * 
	 * @param player
	 */
	public static void removeWaitOrInGamePlayer(Player player) {
		removeWaitPlayer(player);
		removeIngamePlayer(player);
	}

	public static boolean removeWaitPlayer(Player player) {
		boolean success = false;
		Player ret = null;
		if (player != null && player.getRoom() != null) {
			Room room = player.getRoom();
			// 等待中的玩家退出房间
			for (int i = 0; i < room.getWaitPlayers().size(); i++) {
				Player p = room.getWaitPlayers().get(i);
				if (p.getId().equals(player.getId())) {
					ret = room.getWaitPlayers().remove(i);
				}
			}
		}
		if (ret != null) {
			success = true;
		}
		return success;
	}

	public static boolean removeIngamePlayer(Player player) {
		boolean success = false;
		Player ret = null;
		if (player != null && player.getRoom() != null) {
			Room room = player.getRoom();
			// 等待中的玩家退出房间
			for (int i = 0; i < room.getIngamePlayers().size(); i++) {
				Player p = room.getIngamePlayers().get(i);
				if (p.getId().equals(player.getId())) {
					ret = room.getIngamePlayers().remove(i);
				}
			}
		}
		if (ret != null) {
			success = true;
		}
		return success;
	}

	/**
	 * 为房间中正在游戏的玩家分配手牌
	 * 
	 * @param room
	 * @param player
	 */
	public static void assignHandPokerByRoom(Room room) {
		List<Integer> cardList = room.getCardList();
		for (Player p : room.getIngamePlayers()) {
			int[] hankPoker = { cardList.get(0), cardList.get(1) };
			cardList.remove(0);
			cardList.remove(0);
			// 玩家手牌
			p.setHandPokers(hankPoker);
		}
	}

	/**
	 * 发公共牌
	 * 
	 * @param room 房间
	 * @param num  数量
	 */
	public static void assignCommonCardByNum(Room room, int num) {
		List<Integer> cardList = room.getCardList();
		for (int i = 0; i < num; i++) {
			room.getCommunityCards().add(cardList.get(0));
			cardList.remove(0);
		}
		// 通知房间中的每个玩家
		RetMsg retMsg = new RetMsg();
		retMsg.setC("onAssignCommonCard");
		retMsg.setState(1);
		// 所有公共牌
		String message = JsonUtils.toJson(room.getCommunityCards(), room.getCommunityCards().getClass());
		retMsg.setMessage(message);
		String msg = JsonUtils.toJson(retMsg, RetMsg.class);
		sendMsgToPlayerByRoom(room, msg);
	}

	/**
	 * 将一个玩家列表中的玩家全部移动到另一个玩家列表中
	 * 
	 * @param from
	 * @param to
	 */
	public static void movePlayers(List<Player> from, List<Player> to) {
		while (from.size() > 0) {
			to.add(from.get(0));// 添加来源列表的首位到目标列表
			from.remove(0);// 移除来源列表的首位
		}
	}

	/**
	 * 获取房间中的玩家数量
	 * 
	 * @param room
	 * @return
	 */
	public static int getRoomPlayerCount(Room room) {
		int playerCount = room.getWaitPlayers().size() + room.getIngamePlayers().size();
		return playerCount;
	}

	/**
	 * 创建一个相应级别的房间
	 * 
	 * @param level
	 */
	public static Room createRoom(int level) {
		Room room = RoomTypeList.getRoom(level);
		TexasStatic.roomList.add(room);
		return room;
	}

	public static ThreeCardRoom createThreeCardRoomRoom(int level) {
		ThreeCardRoom room = null;
		room = RoomTypeList.getThreeCardRoom(level);
		TexasStatic.threeCardRoomList.add((ThreeCardRoom) room);
		return room;
	}

	/**
	 * 移除没有玩家的空房间
	 */
	public static void removeEmptyRoom() {
		for (int i = 0; i < TexasStatic.roomList.size(); i++) {
			Room room = TexasStatic.roomList.get(i);
			int count = room.getIngamePlayers().size() + room.getWaitPlayers().size();
			if (count == 0) {
				TexasStatic.roomList.remove(i);
			}
		}
	}

	/**
	 * 发送表情或文字
	 * 
	 * @param room
	 * @param msg
	 */
	public static void sendMessage(Session session, String message) {
		Player p = getPlayerBySessionId(session.getId());
		if (p != null) {
			RetMsg retMsg = new RetMsg();
			retMsg.setMessage(message);
			retMsg.setC("onPlayerSendMessage");
			retMsg.setState(1);
			String msg = JsonUtils.toJson(retMsg, RetMsg.class);
			sendMsgToPlayerByRoom(p.getRoom(), msg);
		}
	}

	/**
	 * 给房间中正在游戏的玩家发送消息
	 * 
	 * @param room
	 * @param msg
	 */
	public static void sendMsgToIngamePlayerByRoom(Room room, String msg) {
		sendMsgToList(room.getIngamePlayers(), msg);
	}

	/**
	 * 给房间中处于等待状态的玩家发消息
	 * 
	 * @param room
	 * @param msg
	 */
	public static void sendMsgToWaitPlayerByRoom(Room room, String msg) {
		sendMsgToList(room.getWaitPlayers(), msg);
	}

	/**
	 * 给房间中的每一个玩家发消息
	 * 
	 * @param room
	 * @param msg
	 */
	public static void sendMsgToPlayerByRoom(Room room, String msg) {
		sendMsgToIngamePlayerByRoom(room, msg);
		sendMsgToWaitPlayerByRoom(room, msg);
	}

	/**
	 * 给一组玩家发消息
	 * 
	 * @param playerList
	 * @param msg
	 */
	public static void sendMsgToList(List<Player> playerList, String msg) {
		playerList.parallelStream().forEach(player -> sendMsgToPlayer(player, msg));
		SystemLog.printlog("toAllPlayers:" + msg);
	}

	/**
	 * 给一个玩家发消息,批量发送调用
	 * 
	 * @param player
	 * @param msg
	 */
	public static void sendMsgToPlayer(Player player, String msg) {
		if (player != null && player.getSession() != null) {
			TexasWS.sendText(player.getSession(), msg);
		}
	}

	public static void sendMsgToOne(Player p, String msg) {
		if (p != null) {
			Session session = p.getSession();
			if (session != null) {
				TexasWS.sendText(session, msg);
				SystemLog.printlog("toOne:" + msg);
			}
		}
	}

	public static void sendMsgToOne(Session session, String msg) {
		if (session != null) {
			TexasWS.sendText(session, msg);
			SystemLog.printlog("toOne:" + msg);
		}
	}

	public static Player getPlayerBySessionId(String sessionId) {
		Player p = TexasStatic.loginPlayerMap.get(sessionId);
		return p;
	}

	public static Session getSessionByPlayer(Player p) {
		Session session = p.getSession();
		return session;
	}

	/**
	 * 更新下一个轮到的玩家
	 * 
	 * @param room
	 * @return
	 */
	public static void updateNextTurn(Room room) {
		int thisturn = room.getNextturn();
		// TODO 特殊判断。。。
		thisturn = getNextSeatNum(thisturn, room, true);
		room.setNextturn(thisturn);
	}

	/**
	 * 更新下一个轮到的玩家
	 * 
	 * @param clockwise 是否顺时针
	 * @param room
	 * @return
	 */
	public static void updateNextTurn(Room room, boolean clockwise) {
		int thisturn = room.getNextturn();
		thisturn = getNextSeatNum(thisturn, room, clockwise);
		room.setNextturn(thisturn);
	}

	/**
	 * 获取下一个可操作玩家的座位号
	 * 
	 * @param p
	 */
	public static int getNextSeatNum(int seatNum, Room room) {
		int begin = seatNum;
		while (true) {
			seatNum = getNextNum(seatNum, room);
			Player pi = getPlayerBySeatNum(seatNum, room.getIngamePlayers());
			if (pi != null && !pi.isFold() && pi.getBodyChips() != 0) {
				break;
			}
			// 已经循环一圈
			if (begin == seatNum) {
				break;
			}
		}
		return seatNum;
	}

	/**
	 * 获取下一个可操作玩家的座位号
	 * 
	 * @param clockwise 是否顺时针
	 * @param p
	 */
	public static int getNextSeatNum(int seatNum, Room room, boolean clockwise) {
		int begin = seatNum;
		while (true) {
			seatNum = getNextNum(seatNum, room, clockwise);
			Player pi = getPlayerBySeatNum(seatNum, room.getIngamePlayers());
			if (pi != null && !pi.isFold() && pi.getBodyChips() != 0) {
				break;
			}
			// 已经循环一圈
			if (begin == seatNum) {
				break;
			}
		}
		return seatNum;
	}

	/**
	 * 获取下一个玩家座位号,得到下一个dealer使用
	 * 
	 * @param p
	 */
	public static int getNextSeatNumDealer(int seatNum, Room room) {
		boolean finded = false;
		int begin = seatNum;
		while (!finded) {
			seatNum = getNextNum(seatNum, room);
			for (Player pw : room.getWaitPlayers()) {
				if (pw.getSeatNum() == seatNum) {
					finded = true;
					break;
				}
			}
			for (Player pi : room.getIngamePlayers()) {
				if (pi.getSeatNum() == seatNum) {
					finded = true;
					break;
				}
			}
			// 已经循环一圈
			if (begin == seatNum) {
				break;
			}
		}
		return seatNum;
	}

	/**
	 * 
	 * 返回下一个座位号
	 * 
	 * @param seatNum
	 * @param room
	 * @return
	 */
	private static int getNextNum(int seatNum, Room room) {
		int nextSeatNum = seatNum + 1;
		if (nextSeatNum >= room.getMaxPlayers()) {
			nextSeatNum = 0;
		}
		return nextSeatNum;
	}

	/**
	 * 
	 * 返回下一个座位号
	 * 
	 * @param clockwise 是否顺时针
	 * @param seatNum
	 * @param room
	 * @return
	 */
	private static int getNextNum(int seatNum, Room room, boolean clockwise) {
		if (clockwise) {
			return getNextNum(seatNum, room);
		} else {
			int nextSeatNum = seatNum - 1;
			if (nextSeatNum < 0) {
				nextSeatNum = room.getMaxPlayers() - 1;
			}
			return nextSeatNum;
		}
	}

	/**
	 * 根据座位号返回玩家
	 * 
	 * @param seatNum
	 * @param room
	 * @return
	 */
	public static Player getPlayerBySeatNum(int seatNum, List<Player> playerList) {
		Optional<Player> player = null;
		player = playerList.parallelStream().filter(p -> p.getSeatNum() == seatNum).findFirst();
		if (player.isPresent()) {
			return player.get();
		} else {
			return null;
		}
	}

	/**
	 * 每局开始时更新下一个dealer
	 * 
	 * @param room
	 * @return
	 */
	public static void updateNextDealer(Room room) {
		int d = room.getDealer();
		d = getNextSeatNumDealer(d, room);
		room.setDealer(d);
	}

	/**
	 * 改变玩家chips的方法
	 * 
	 * @param p
	 * @param chips
	 */
	public static void changePlayerChips(Player p, Long chips) {
		synchronized (p) {
			p.setBodyChips(p.getBodyChips() + chips);
		}
	}

	/**
	 * 按值排序一个map
	 * 
	 * @param oriMap
	 * @return
	 */
	public static Map<Integer, Long> sortMapByValue(Map<Integer, Long> oriMap) {
		Map<Integer, Long> sortedMap = new LinkedHashMap<Integer, Long>();
		if (oriMap != null && !oriMap.isEmpty()) {
			List<Map.Entry<Integer, Long>> entryList = new ArrayList<Map.Entry<Integer, Long>>(oriMap.entrySet());
			Collections.sort(entryList, new Comparator<Map.Entry<Integer, Long>>() {
				public int compare(Entry<Integer, Long> entry1, Entry<Integer, Long> entry2) {
					Long value1 = 0l, value2 = 0l;
					value1 = entry1.getValue();
					value2 = entry2.getValue();
					return value1.compareTo(value2);
				}
			});
			Iterator<Map.Entry<Integer, Long>> iter = entryList.iterator();
			Map.Entry<Integer, Long> tmpEntry = null;
			while (iter.hasNext()) {
				tmpEntry = iter.next();
				sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
			}
		}
		return sortedMap;
	}

	/**
	 * 求Map<Integer, Long>中Value(值)的最大值
	 * 
	 * @param map
	 * @return
	 */
	public static Long getMaxValue(Map<Integer, Long> map) {
		if (map == null)
			return null;
		Collection<Long> c = map.values();
		Object[] obj = c.toArray();
		Arrays.sort(obj);
		return (Long) obj[obj.length - 1];
	}

	/**
	 * 进入房间
	 * 
	 * @param session
	 * @param message
	 */
	public static void inRoom(Session session, String message) {
		Room roomMessage = getRoomMessage(message);

		Player currPlayer = getPlayerBySessionId(session.getId());
		// 进入房间一个非机器人，则进入机器人陪玩
		if (!currPlayer.getUsername().contains("robot")) {
			Date now = new Date();
			//// 创建2机器人
			RobotManager.init(2);
			Date costEnd = new Date();
			long cost = costEnd.getTime() - now.getTime();
			if (cost > 100) {
				SystemLog.printPerformance("add robot:" + message + " cost Millisecond" + cost);
			}
		}

		// 从数据库重新更新玩家筹码

		Player upPlayer = new Player();
		upPlayer.setId(currPlayer.getId());
		PlayerService pservice = (PlayerService) SpringUtil.getBean("playerService");
		upPlayer = pservice.selectPlayer(upPlayer);
		long bodyChips = currPlayer.getBodyChips();
		long restChips = upPlayer.getChips() - bodyChips;
		currPlayer.setChips(restChips);

		// 查找空房间，没有则创建新房间
		Room usableRoom = getUsableRoom(roomMessage.getLevel(), currPlayer, roomMessage.getType());

		RetMsg rm = new RetMsg();
		rm.setC("onEnterRoom");
		rm.setState(1);
		if (usableRoom == null) {
			rm.setState(0);
			rm.setMessage("筹码不足");
			String retMsg = JsonUtils.toJson(rm, RetMsg.class);
			sendMsgToOne(currPlayer, retMsg);
			return;
		}

		if (currPlayer == null) {
			rm.setState(0);
			rm.setMessage("请先登录");
		} else {
			// 如果玩家已在房间中，则先将其从房间中移除
			outRoom(currPlayer);
			boolean inRoomResult = inRoom(usableRoom, currPlayer);
			if (!inRoomResult) {
				// 加入失败则创建新房间并加入
				usableRoom = createRoomThenIn(roomMessage.getLevel(), currPlayer, roomMessage.getType());
			}

			String roominfo = JsonUtils.toJson(usableRoom, Room.class);
			rm.setMessage(roominfo);
			// 通知玩家加入房间成功
			String retMsg = JsonUtils.toJson(rm, RetMsg.class);
			sendMsgToOne(currPlayer, retMsg);
			// 通知所有房间内玩家，有玩家加入
			sendPlayerToOthers(currPlayer, usableRoom, "onPlayerEnterRoom");
			// 检查房间是否可以开始游戏,一秒等待
			usableRoom.checkStart(800);
		}
	}

	/**
	 * 退出房间
	 * 
	 * @param session
	 * @param message
	 * @param sendOrNot 是否向退出房间的玩家发送退出成功消息
	 */
	public static void outRoom(Session session, String message, boolean sendOrNot) {
		Player p = TexasStatic.loginPlayerMap.get(session.getId());

		if (sendOrNot) {
			// 告诉自己离开
			RetMsg rm = new RetMsg();
			rm.setC("onOutRoom");
			rm.setState(1);
			rm.setMessage(JsonUtils.toJson(p, Player.class));
			String retMsg = JsonUtils.toJson(rm, RetMsg.class);
			sendMsgToOne(p, retMsg);
		}
		// 告诉其他玩家有人离开
		outRoom(p);

	}

	public static Room getRoomMessage(String message) {
		Room room = JsonUtils.fromJson(message, Room.class);
		return room;
	}

	/**
	 * 向除currPlayer之外的玩家发送currPlayer玩家信息
	 * 
	 * @param currPlayer
	 * @param room
	 * @param c
	 */
	public static void sendPlayerToOthers(Player currPlayer, Room room, String c) {
		String currPlayerInfo = JsonUtils.toJson(currPlayer, Player.class);
		RetMsg rm_inRoom = new RetMsg();
		rm_inRoom.setC(c);
		rm_inRoom.setState(1);
		rm_inRoom.setMessage(currPlayerInfo);
		String inRoomMessage = JsonUtils.toJson(rm_inRoom, RetMsg.class);
		sendMessageToOtherPlayers(currPlayer.getId(), room, inRoomMessage);
	}

	/**
	 * 向除currPlayer之外的玩家发送message
	 * 
	 * @param selfId
	 * @param room
	 * @param message
	 */
	public static void sendMessageToOtherPlayers(String selfId, Room room, String message) {
		if (room == null) {
			return;
		}
		// 通知其他玩家
		List<Player> waitPlayers = room.getWaitPlayers();
		for (Player p : waitPlayers) {
			if (p != null && p.getId() != selfId) {
				Session _session = getSessionByPlayer(p);
				TexasWS.sendText(_session, message);
			}

		}
		List<Player> ingamePlayers = room.getIngamePlayers();
		for (Player p : ingamePlayers) {
			if (p != null && p.getId() != selfId) {
				Session _session = getSessionByPlayer(p);
				TexasWS.sendText(_session, message);
			}

		}
	}
}
