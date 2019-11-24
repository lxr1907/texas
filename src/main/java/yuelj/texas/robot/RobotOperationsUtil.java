package yuelj.texas.robot;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import yuelj.entity.BetPlayer;
import yuelj.entity.Player;
import yuelj.entity.PrivateRoom;
import yuelj.entity.RetMsg;
import yuelj.utils.RandomNumUtil;
import yuelj.utils.serialize.JsonUtils;

public class RobotOperationsUtil {
	private static Logger logger = LogManager.getLogger(RobotOperationsUtil.class);
	// 机器人账号的前缀
	private static final String robotAccountPre = "robot";
	// 机器人密码的前缀
	private static final String robotAccountPasswordPre = "A0858374F309F2DFA1F46EC94DEA6EBE";
	private static ConcurrentLinkedQueue<Integer> robotAccountQueue = new ConcurrentLinkedQueue<Integer>();
	static {
		// 初始化2000个机器人id
		for (int i = 0; i < 2000; i++) {
			robotAccountQueue.offer(i);
		}
	}

	/**
	 * 机器人注册
	 * 
	 * @param robotClient
	 */
	public static void robotRegist(RobotWsClient robotClient) {
		RobotPlayer robotPlayer = new RobotPlayer();
		robotPlayer.setC(0);
		robotPlayer.setUsername(robotAccountPre + robotAccountQueue.poll());
		robotPlayer.setUserpwd(robotAccountPasswordPre + robotPlayer.getUsername());
		robotClient.player = robotPlayer;
		robotClient.sendText(JsonUtils.toJson(robotPlayer, RobotPlayer.class));
	}

	/**
	 * 机器人登陆
	 * 
	 * @param robotClient
	 */
	public static void robotLogin(RobotWsClient robotClient) {
		RobotPlayer robotPlayer = new RobotPlayer();
		robotPlayer.setC(1);
		int id = robotAccountQueue.poll();
		robotPlayer.setRobotAccountId(id);
		robotPlayer.setUsername(robotAccountPre + id);
		robotPlayer.setUserpwd(robotAccountPasswordPre + robotPlayer.getUsername());
		robotPlayer.setRobotStart(new Date());
		robotClient.player = robotPlayer;
		robotClient.sendText(JsonUtils.toJson(robotPlayer, RobotPlayer.class));
	}

	/**
	 * 机器人加入房间
	 * 
	 * @param robotClient
	 */
	public static void robotEntorRoom(RobotWsClient robotClient) {
		RobotPlayer robotPlayer = new RobotPlayer();
		robotPlayer.setC(2);
		// 要加入的房间级别
		robotPlayer.setLevel("0");
		robotClient.sendText(JsonUtils.toJson(robotPlayer, RobotPlayer.class));
	}

	/**
	 * 机器人退出
	 * 
	 * @param robotClient
	 */
	public static void robotOut(RobotWsClient robotClient) {
		RobotPlayer robotPlayer = robotClient.player;
		// 退出房间
		robotPlayer.setC(3);
		robotClient.sendText(JsonUtils.toJson(robotPlayer, RobotPlayer.class));
		// 关闭连接
		try {
			robotClient.session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 归还robotId
		int id = robotPlayer.getRobotAccountId();
		robotAccountQueue.offer(id);
	}

	public static void onLogin(RobotWsClient robotClient, RetMsg retMsg) {
		// 登陆成功则加入房间
		if (retMsg.getState() == 1) {
			String myInfo = retMsg.getMessage();
			robotClient.player = JsonUtils.fromJson(myInfo, RobotPlayer.class);
			robotEntorRoom(robotClient);
		}
	}

	public static void onEnterRoom(RobotWsClient robotClient, RetMsg retMsg) {
		// 成功加入房间
		if (retMsg.getState() == 1) {
			String roominfo = retMsg.getMessage();
			robotClient.roomInfo = JsonUtils.fromJson(roominfo, PrivateRoom.class);
			// 设置座位号
			for (Player p : robotClient.roomInfo.getWaitPlayers()) {
				if (p.getId().equals(robotClient.player.getId())) {
					robotClient.player.setSeatNum(p.getSeatNum());
				}
			}
			for (Player p : robotClient.roomInfo.getIngamePlayers()) {
				if (p.getId().equals(robotClient.player.getId())) {
					robotClient.player.setSeatNum(p.getSeatNum());
				}
			}
		}
	}

	/**
	 * 接收开始游戏消息
	 * 
	 * @param robotClient
	 * @param retMsg
	 */
	public static void onGameStart(RobotWsClient robotClient, RetMsg retMsg) {
		// 游戏开始
		if (retMsg.getState() == 1) {
			String roomInfoStr = retMsg.getMessage();
			PrivateRoom roomInfo = JsonUtils.fromJson(roomInfoStr, PrivateRoom.class);
			robotClient.setRoomInfo(roomInfo);
			// 手牌
			robotClient.player.setHandPokers(roomInfo.getHandPokers());
			// 身上筹码
			for (Player p : roomInfo.getIngamePlayers()) {
				if (p.getId().equals(robotClient.player.getId())) {
					robotClient.player.setBodyChips(p.getBodyChips());
				}
			}
			// 房间信息
			int turn = roomInfo.getNextturn();
			if (turn == robotClient.player.getSeatNum()) {
				robotOpt(robotClient);
			}
		}
	}

	/**
	 * 轮到某个玩家
	 * 
	 * @param robotClient
	 * @param retMsg
	 */
	public static void onPlayerTurn(RobotWsClient robotClient, RetMsg retMsg) {
		// 轮到某个玩家操作
		if (retMsg.getState() == 1) {
			// 轮到我操作
			if (retMsg.getMessage().equals(robotClient.player.getSeatNum() + "")) {
				robotOpt(robotClient);
			}
		}
	}

	/**
	 * 有玩家下注
	 * 
	 * @param robotClient
	 * @param retMsg
	 */
	public static void onPlayerBet(RobotWsClient robotClient, RetMsg retMsg) {
		// 轮到某个玩家操作
		if (retMsg.getState() == 1) {
			BetPlayer bp = new BetPlayer();
			bp = JsonUtils.fromJson(retMsg.getMessage(), BetPlayer.class);
			Long oldBet = 0l;
			if (bp == null) {
				return;
			}
			if (robotClient != null && robotClient.roomInfo != null
					&& robotClient.roomInfo.getBetRoundMap().get(bp.getSeatNum()) != null) {
				oldBet = robotClient.roomInfo.getBetRoundMap().get(bp.getSeatNum());
			}
			Long chips = oldBet + bp.getInChips();
			robotClient.getRoomInfo().getBetRoundMap().put(bp.getSeatNum(), chips);
			// 设置本轮最大加注
			if (chips > robotClient.getRoomInfo().getRoundMaxBet()) {
				robotClient.getRoomInfo().setRoundMaxBet(chips.intValue());
			}
			for (Player p : robotClient.roomInfo.getIngamePlayers()) {
				if (p.getSeatNum() == bp.getSeatNum()) {
					p.setBodyChips(p.getBodyChips() - bp.getInChips());
					break;
				}
			}
		}
	}

	/**
	 * 游戏结束时，判断自己是否该退出房间了
	 * 
	 * @param robotClient
	 * @param retMsg
	 */
	public static void onGameEnd(RobotWsClient robotClient, RetMsg retMsg) {
		// 当该机器人的退出时间到，则退出房间
		if (robotClient.getLogOutTime().before(new Date())) {
			robotOut(robotClient);
		}
	}

	/**
	 * 轮到机器人操作
	 * 
	 * @param robotClient
	 */
	public static void robotOpt(RobotWsClient robotClient) {
		Random r = new Random();
		// 思考时间，1到2秒
		int second = r.nextInt(1) + 1;
		try {
			Thread.sleep(second * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long callNeed = 0;
		long maxBet = robotClient.getRoomInfo().getRoundMaxBet();
		long mybet = 0;
		long bigbet = robotClient.getRoomInfo().getBigBet();
		// 计算call或check需要的下注
		for (Map.Entry<Integer, Long> entry : robotClient.roomInfo.getBetRoundMap().entrySet()) {
			if (entry.getKey().equals(robotClient.player.getSeatNum())) {
				if (entry.getValue() != null) {
					mybet = entry.getValue();
				}
			}
		}
		callNeed = maxBet - mybet;
		logger.info("callNeed:" + callNeed + "maxBet:" + maxBet + "myBet:" + mybet);
		// 获取10到99随机数
		int randomNum = RandomNumUtil.getNextInt(2);
		// 一定概率加注2倍bigbet
		int followBet = (int) callNeed;
		int addBet = (int) ((bigbet * 2 + callNeed) );
		if (randomNum < 40 && callNeed > 0) {
			// 一定概率fold
			fold(robotClient);
		} else if (randomNum < 80) {
			if (followBet > 0) {
				// logger.error("callNeed:" + callNeed + "maxBet:" + maxBet + "myBet:" +
				// mybet+",followBet:"+followBet);
				// 可以跟注，跟注
				betChips(robotClient, followBet);
			} else {
				// 可以check的情况下
				check(robotClient);
			}
		} else if (randomNum < 96) {
			if (addBet > 0) {
				// logger.error("callNeed:" + callNeed + "maxBet:" + maxBet + "myBet:" +
				// mybet+",addBet:"+addBet);
				betChips(robotClient, addBet);
			} else {
				// 可以check的情况下
				check(robotClient);
			}
		} else {
			// 一定概率allin
			addBet = (int) robotClient.player.getBodyChips();
			betChips(robotClient, addBet);
		}
	}

	public static void fold(RobotWsClient robotClient) {
		Player ret = new Player();
		ret.setC(8);
		robotClient.sendText(JsonUtils.toJson(ret, Player.class));
	}

	public static void check(RobotWsClient robotClient) {
		Player ret = new Player();
		ret.setC(6);
		robotClient.sendText(JsonUtils.toJson(ret, Player.class));
	}

	/**
	 * 下注
	 * 
	 * @param robotClient
	 * @param chips
	 */
	public static void betChips(RobotWsClient robotClient, int chips) {
		// 当call需要的值大于身上携带
		if (chips > robotClient.player.getBodyChips()) {
			logger.info("chips > robotClient.player.getBodyChips()  getBodyChips:" + robotClient.player.getBodyChips()
					+ "chips" + chips);
			chips = (int) robotClient.player.getBodyChips();
		}
		// 下注时减少自己身上的筹码
		int leftChips = (int) (robotClient.player.getBodyChips() - chips);
		robotClient.player.setBodyChips(leftChips);
		Player ret = new Player();
		ret.setC(7);
		ret.setInChips(chips);
		robotClient.sendText(JsonUtils.toJson(ret, Player.class));
	}

	public static void main(String[] args) {
		// 机器人注册
		for (int i = 0; i < 2000; i++) {
			RobotWsClient client = new RobotWsClient(false);
			robotRegist(client);
		}
	}
}
