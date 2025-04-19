package com.lxrtalk.texas.texas;

import java.util.*;
import java.util.Map.Entry;

import com.lxrtalk.texas.texas.threeCard.ThreeCardTexasRoom;
import jakarta.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxrtalk.texas.action.websocket.TexasWS;
import com.lxrtalk.texas.constants.RoomTypeList;
import com.lxrtalk.texas.entity.Player;
import com.lxrtalk.texas.entity.PrivateRoom;
import com.lxrtalk.texas.entity.RetMsg;
import com.lxrtalk.texas.service.PlayerService;
import com.lxrtalk.texas.texas.robot.RobotManager;
import com.lxrtalk.texas.utils.SpringUtil;
import com.lxrtalk.texas.utils.serialize.JsonUtils;

public class TexasUtil {
    private static final Logger logger = LogManager.getLogger(TexasUtil.class);

    /**
     * 获取一个对应级别的可用房间，直接进入
     *
     * @param level
     * @return
     */
    public static TexasRoom getUsableRoomThenIn(int level, Player p) {
        for (int i = 0; i < TexasStatic.texasRoomList.size(); i++) {
            int roomState = TexasStatic.texasRoomList.get(i).getRoomstate();
            if (roomState == 1 && TexasStatic.texasRoomList.get(i).getLevel() == level) {
                boolean success = inRoom(TexasStatic.texasRoomList.get(i), p);
                if (success) {
                    return TexasStatic.texasRoomList.get(i);
                }
            }
        }
        return createRoomByPlayer(level, p);
    }

    /**
     * 获取可用房间类型
     *
     * @param type 1宝鸡拼三张，不传默认德州扑克
     */
    public static TexasRoom getUsableRoomThenIn(int level, Player p, int type) {
        // 加入游戏类型，1宝鸡拼三张，不传默认德州扑克
        if (type == 1) {
            ThreeCardTexasRoom usableroom = null;
            ThreeCardTexasRoom roomConfig = RoomTypeList.threeCardRoomTypeMap.get(level);
            if (p.getChips() < roomConfig.getConfig().getMinChips()) {
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
            return getUsableRoomThenIn(level, p);
        }
    }

    public static TexasRoom createRoomThenIn(int level, Player player, int type) {
        if (type == 1) {
            ThreeCardTexasRoom newRoom = createThreeCardRoomRoom(level);
            inRoom(newRoom, player);
            TexasStatic.threeCardRoomList.add(newRoom);
            return newRoom;
        } else {
            return createRoomByPlayer(level, player);
        }
    }

    /**
     * 进入房间
     *
     * <pre>
     * 1，检查房间是否还可加入 2，加入房间/重新查找可以进入的房间 3，改变房间状态
     */
    public static boolean inRoom(TexasRoom texasRoom, Player player) {
        if (texasRoom == null || player == null) {
            return false;
        }
        // 如果玩家已在房间中，则先出房间
        outRoom(player);
        if (texasRoom.getRoomstate() == 0) {
            return false;
        }
        // 房间加锁
        synchronized (texasRoom.getFreeSeatStack()) {
            // 房间满人，修改状态为不可加入, 加入房间失败
            if (texasRoom.getFreeSeatStack().isEmpty()) {
                texasRoom.setRoomstate(0);
                return false;
            }
            texasRoom.getWaitPlayers().add(player);
            // 设定座位号
            int seatNum = texasRoom.getFreeSeatStack().pop();// 从空闲座位的栈中取出一个座位
            player.setSeatNum(seatNum);
            if (texasRoom.getFreeSeatStack().isEmpty()) {
                // 房间满人，修改状态为不可加入
                texasRoom.setRoomstate(0);
            }
            texasRoom.assignChipsForInRoom(player);
            // 成功则设置房间
            player.setRoom(texasRoom);
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
            TexasRoom texasRoom = player.getRoom();
            // 通知所有房间内玩家，有玩家离开
            sendPlayerToOthers(player, texasRoom, "onPlayerLeaveRoom");
            removeWaitOrInGamePlayer(player);
            // 成功则设置房间
            int index = texasRoom.donePlayerList.indexOf(player.getSeatNum());
            if (index != -1) {
                texasRoom.donePlayerList.remove(index);
            }
            // 记录玩家的筹码变化
            texasRoom.assignChipsForOutRoom(player);
            // 还座位号
            if (player.getSeatNum() != -1) {
                texasRoom.getFreeSeatStack().push(player.getSeatNum());
                player.setSeatNum(-1);
            }
            // 修改房间状态为可加入
            texasRoom.setRoomstate(1);
            // 在游戏中的玩家数少于最低玩家数时结束游戏
            texasRoom.checkEnd();
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

    public static void removeWaitPlayer(Player player) {
        if (player != null && player.getRoom() != null) {
            TexasRoom texasRoom = player.getRoom();
            // 等待中的玩家退出房间
            for (int i = 0; i < texasRoom.getWaitPlayers().size(); i++) {
                Player p = texasRoom.getWaitPlayers().get(i);
                if (p.getId().equals(player.getId())) {
                    texasRoom.getWaitPlayers().remove(i);
                    break;
                }
            }
        }
    }

    public static void removeIngamePlayer(Player player) {
        if (player != null && player.getRoom() != null) {
            TexasRoom texasRoom = player.getRoom();
            // 等待中的玩家退出房间
            for (int i = 0; i < texasRoom.getIngamePlayers().size(); i++) {
                Player p = texasRoom.getIngamePlayers().get(i);
                if (p.getId().equals(player.getId())) {
                    texasRoom.getIngamePlayers().remove(i);
                    break;
                }
            }
        }
    }

    /**
     * 为房间中正在游戏的玩家分配手牌
     */
    public static void assignHandPokerByRoom(TexasRoom texasRoom) {
        List<Integer> cardList = texasRoom.getCardList();
        for (Player p : texasRoom.getIngamePlayers()) {
            int[] hankPoker = {cardList.get(0), cardList.get(1)};
            cardList.remove(0);
            cardList.remove(0);
            // 玩家手牌
            p.setHandPokers(hankPoker);
        }
    }

    /**
     * 发公共牌
     *
     * @param texasRoom 房间
     * @param num  数量
     */
    public static void assignCommonCardByNum(TexasRoom texasRoom, int num) {
        List<Integer> cardList = texasRoom.getCardList();
        for (int i = 0; i < num; i++) {
            texasRoom.getCommunityCards().add(cardList.get(0));
            cardList.remove(0);
        }
        // 通知房间中的每个玩家
        RetMsg retMsg = new RetMsg();
        retMsg.setC("onAssignCommonCard");
        retMsg.setState(1);
        // 所有公共牌
        String message = JsonUtils.toJson(texasRoom.getCommunityCards(), texasRoom.getCommunityCards().getClass());
        retMsg.setMessage(message);
        String msg = JsonUtils.toJson(retMsg, RetMsg.class);
        sendMsgToPlayerByRoom(texasRoom, msg);
    }

    /**
     * 将一个玩家列表中的玩家全部移动到另一个玩家列表中
     *
     * @param from
     * @param to
     */
    public static void movePlayers(List<Player> from, List<Player> to) {
        while (!from.isEmpty()) {
            to.add(from.get(0));// 添加来源列表的首位到目标列表
            from.remove(0);// 移除来源列表的首位
        }
    }

    public static void moveMaxPlayers(List<Player> from, List<Player> to, int max) {
        while (!from.isEmpty() && to.size() < max) {
            to.add(from.get(0));// 添加来源列表的首位到目标列表
            from.remove(0);// 移除来源列表的首位
        }
    }

    /**
     * 获取房间中的玩家数量
     *
     * @param texasRoom
     * @return
     */
    public static int getRoomPlayerCount(TexasRoom texasRoom) {
        int playerCount = texasRoom.getWaitPlayers().size() + texasRoom.getIngamePlayers().size();
        return playerCount;
    }

    /**
     * 创建一个相应级别的房间
     */
    public static TexasRoom createRoom(int level) {
        TexasRoom texasRoom = RoomTypeList.getNewRoom(level);
        TexasStatic.texasRoomList.add(texasRoom);
        return texasRoom;
    }

    /**
     * 创建一个相应级别的房间,玩家直接进入
     *
     * @param level
     */
    public static TexasRoom createRoomByPlayer(int level, Player player) {
        TexasRoom texasRoom = RoomTypeList.getNewRoom(level);
        inRoom(texasRoom, player);
        TexasStatic.texasRoomList.add(texasRoom);
        return texasRoom;
    }

    public static ThreeCardTexasRoom createThreeCardRoomRoom(int level) {
        ThreeCardTexasRoom room = null;
        room = RoomTypeList.getThreeCardRoom(level);
        TexasStatic.threeCardRoomList.add((ThreeCardTexasRoom) room);
        return room;
    }

    /**
     * 移除没有玩家的空房间
     */
    public static void removeEmptyRoom() {
        for (int i = 0; i < TexasStatic.texasRoomList.size(); i++) {
            TexasRoom texasRoom = TexasStatic.texasRoomList.get(i);
            int count = texasRoom.getIngamePlayers().size() + texasRoom.getWaitPlayers().size();
            if (count == 0) {
                TexasStatic.texasRoomList.remove(i);
            }
        }
    }

    /**
     * 发送表情或文字
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
     * @param texasRoom
     * @param msg
     */
    public static void sendMsgToIngamePlayerByRoom(TexasRoom texasRoom, String msg) {
        sendMsgToList(texasRoom.getIngamePlayers(), msg);
    }

    /**
     * 给房间中处于等待状态的玩家发消息
     *
     * @param texasRoom
     * @param msg
     */
    public static void sendMsgToWaitPlayerByRoom(TexasRoom texasRoom, String msg) {
        sendMsgToList(texasRoom.getWaitPlayers(), msg);
    }

    /**
     * 给房间中的每一个玩家发消息
     *
     * @param texasRoom
     * @param msg
     */
    public static void sendMsgToPlayerByRoom(TexasRoom texasRoom, String msg) {
        sendMsgToIngamePlayerByRoom(texasRoom, msg);
        sendMsgToWaitPlayerByRoom(texasRoom, msg);
    }

    /**
     * 给一组玩家发消息
     *
     * @param playerList
     * @param msg
     */
    public static void sendMsgToList(List<Player> playerList, String msg) {
        playerList.parallelStream().forEach(player -> sendMsgToPlayer(player, msg));
        logger.info("toAllPlayers:" + msg);
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
                logger.info("toOne:" + msg);
            }
        }
    }

    public static void sendMsgToOne(Session session, String msg) {
        if (session != null) {
            TexasWS.sendText(session, msg);
            logger.info("toOne:" + msg);
        }
    }

    public static Player getPlayerBySessionId(String sessionId) {
        Player p = TexasStatic.loginPlayerMap.get(sessionId);
        return p;
    }

    /**
     * 更新下一个轮到的玩家
     *
     * @param texasRoom
     * @return
     */
    public static void updateNextTurn(TexasRoom texasRoom) {
        int thisturn = texasRoom.getNextTurn();
        // TODO 特殊判断。。。
        thisturn = getNextSeatNum(thisturn, texasRoom, true);
        texasRoom.setNextTurn(thisturn);
    }

    /**
     * 更新下一个轮到的玩家
     *
     * @param clockwise 是否顺时针
     * @param texasRoom
     * @return
     */
    public static void updateNextTurn(TexasRoom texasRoom, boolean clockwise) {
        int thisturn = texasRoom.getNextTurn();
        thisturn = getNextSeatNum(thisturn, texasRoom, clockwise);
        texasRoom.setNextTurn(thisturn);
    }

    /**
     * 获取下一个可操作玩家的座位号
     */
    public static int getNextSeatNum(int seatNum, TexasRoom texasRoom) {
        int begin = seatNum;
        while (true) {
            seatNum = getNextNum(seatNum, texasRoom);
            Player pi = getPlayerBySeatNum(seatNum, texasRoom.getIngamePlayers());
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
     */
    public static int getNextSeatNum(int seatNum, TexasRoom texasRoom, boolean clockwise) {
        int begin = seatNum;
        while (true) {
            seatNum = getNextNum(seatNum, texasRoom, clockwise);
            Player pi = getPlayerBySeatNum(seatNum, texasRoom.getIngamePlayers());
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
     */
    public static int getNextSeatNumDealer(int seatNum, TexasRoom texasRoom) {
        boolean finded = false;
        int begin = seatNum;
        while (!finded) {
            seatNum = getNextNum(seatNum, texasRoom);
            for (Player pw : texasRoom.getWaitPlayers()) {
                if (pw.getSeatNum() == seatNum) {
                    finded = true;
                    break;
                }
            }
            for (Player pi : texasRoom.getIngamePlayers()) {
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
     * 返回下一个座位号
     *
     * @param seatNum
     * @param texasRoom
     * @return
     */
    private static int getNextNum(int seatNum, TexasRoom texasRoom) {
        int nextSeatNum = seatNum + 1;
        if (nextSeatNum >= texasRoom.getConfig().getMaxPlayers()) {
            nextSeatNum = 0;
        }
        return nextSeatNum;
    }

    /**
     * 返回下一个座位号
     *
     * @param clockwise 是否顺时针
     * @param seatNum
     * @param texasRoom
     * @return
     */
    private static int getNextNum(int seatNum, TexasRoom texasRoom, boolean clockwise) {
        if (clockwise) {
            return getNextNum(seatNum, texasRoom);
        } else {
            int nextSeatNum = seatNum - 1;
            if (nextSeatNum < 0) {
                nextSeatNum = texasRoom.getConfig().getMaxPlayers() - 1;
            }
            return nextSeatNum;
        }
    }

    /**
     * 根据座位号返回玩家
     */
    public static Player getPlayerBySeatNum(int seatNum, List<Player> playerList) {
        Optional<Player> player;
        player = playerList.parallelStream().filter(p -> p.getSeatNum() == seatNum).findFirst();
        return player.orElse(null);
    }

    /**
     * 每局开始时更新下一个dealer
     */
    public static void updateNextDealer(TexasRoom texasRoom) {
        int d = texasRoom.getDealer();
        d = getNextSeatNumDealer(d, texasRoom);
        texasRoom.setDealer(d);
    }

    /**
     * 改变玩家chips的方法
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
        oriMap.clear();
        return sortedMap;
    }

    /**
     * 进入房间
     *
     * @param session
     * @param message
     */
    public static void inRoom(Session session, String message) {
        TexasRoom texasRoomMessage = getRoomMessage(message);
        RetMsg rm = new RetMsg();
        rm.setC("onEnterRoom");
        rm.setState(1);
        Player currPlayer = getPlayerBySessionId(session.getId());
        if (currPlayer == null) {
            rm.setState(0);
            String retMsg = JsonUtils.toJson(rm, RetMsg.class);
            rm.setMessage("请先登录");
            sendMsgToOne(currPlayer, retMsg);
            return;
        }
        // 进入房间一个非机器人，则进入机器人陪玩
        if (!currPlayer.getUsername().contains("robot")) {
            Date now = new Date();
            //// 创建机器人
            RobotManager.start(RobotManager.MAX_ROBOT_COUNT);
            Date costEnd = new Date();
            long cost = costEnd.getTime() - now.getTime();
            if (cost > 100) {
                logger.error("add robot:" + message + " cost Millisecond" + cost);
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

        TexasRoomConfig texasRoomConfig = RoomTypeList.roomTypeMap.get(texasRoomMessage.getLevel());

        if (currPlayer.getChips() < texasRoomConfig.getMinChips()) {
            rm.setState(0);
            rm.setMessage("筹码不足");
            String retMsg = JsonUtils.toJson(rm, RetMsg.class);
            sendMsgToOne(currPlayer, retMsg);
            return;
        }
        // 查找空房间，没有则创建新房间
        TexasRoom usableTexasRoom = getUsableRoomThenIn(texasRoomMessage.getLevel(), currPlayer, texasRoomMessage.getType());
        PrivateRoom pRoom = new PrivateRoom();
        pRoom.setRoom(usableTexasRoom);
        String roominfo = JsonUtils.toJson(pRoom, PrivateRoom.class);
        rm.setMessage(roominfo);
        // 通知玩家加入房间成功
        String retMsg = JsonUtils.toJson(rm, RetMsg.class);
        sendMsgToOne(currPlayer, retMsg);
        // 通知所有房间内玩家，有玩家加入
        sendPlayerToOthers(currPlayer, usableTexasRoom, "onPlayerEnterRoom");
        // 检查房间是否可以开始游戏,一秒等待
        usableTexasRoom.checkStart(800);
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

    public static TexasRoom getRoomMessage(String message) {
        return JsonUtils.fromJson(message, TexasRoom.class);
    }

    /**
     * 向除currPlayer之外的玩家发送currPlayer玩家信息
     */
    public static void sendPlayerToOthers(Player currPlayer, TexasRoom texasRoom, String c) {
        String currPlayerInfo = JsonUtils.toJson(currPlayer, Player.class);
        RetMsg rm_inRoom = new RetMsg();
        rm_inRoom.setC(c);
        rm_inRoom.setState(1);
        rm_inRoom.setMessage(currPlayerInfo);
        String inRoomMessage = JsonUtils.toJson(rm_inRoom, RetMsg.class);
        sendMessageToOtherPlayers(currPlayer.getId(), texasRoom, inRoomMessage);
    }

    /**
     * 向除currPlayer之外的玩家发送message
     */
    public static void sendMessageToOtherPlayers(String selfId, TexasRoom texasRoom, String message) {
        if (texasRoom == null) {
            return;
        }
        // 通知其他玩家
        List<Player> waitPlayers = texasRoom.getWaitPlayers();
        for (Player p : waitPlayers) {
            if (p != null && !Objects.equals(p.getId(), selfId)) {
                Session _session = p.getSession();
                TexasWS.sendText(_session, message);
            }

        }
        List<Player> ingamePlayers = texasRoom.getIngamePlayers();
        for (Player p : ingamePlayers) {
            if (p != null && !Objects.equals(p.getId(), selfId)) {
                Session _session = p.getSession();
                TexasWS.sendText(_session, message);
            }

        }
    }
}
