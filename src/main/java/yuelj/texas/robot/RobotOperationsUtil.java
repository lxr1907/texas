package yuelj.texas.robot;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import yuelj.entity.BetPlayer;
import yuelj.entity.Player;
import yuelj.entity.PrivateRoom;
import yuelj.entity.RetMsg;
import yuelj.texas.Room;
import yuelj.utils.RandomNumUtil;
import yuelj.utils.logs.SystemLog;
import yuelj.utils.serialize.JsonUtils;

public class RobotOperationsUtil {
	// 机器人账号的前缀
	private static final String robotAccountPre = "robot";
	// 机器人密码的前缀
	private static final String robotAccountPasswordPre = "A0858374F309F2DFA1F46EC94DEA6EBE";
	private static ConcurrentLinkedQueue<Integer> robotAccountQueue = new ConcurrentLinkedQueue<Integer>();
	static {
		// 初始化120个机器人id
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
			String roominfo = retMsg.getMessage();
			PrivateRoom proom = JsonUtils.fromJson(roominfo, PrivateRoom.class);
			// 手牌
			robotClient.player.setHandPokers(proom.getHandPokers());
			// 身上筹码
			for (Player p : proom.getIngamePlayers()) {
				if (p.getId().equals(robotClient.player.getId())) {
					robotClient.player.setBodyChips(p.getBodyChips());
				}
			}
			// 房间信息
			int turn = proom.getNextturn();
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
			robotClient.roomInfo.getBetRoundMap().put(bp.getSeatNum(), chips);
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
		long maxBet = 0;
		long mybet = 0;
		long bigbet=robotClient.getRoomInfo().getBigBet();
		// 计算call或check需要的下注
		for (Map.Entry<Integer, Long> entry : robotClient.roomInfo.getBetRoundMap().entrySet()) {
			SystemLog.printlog(entry.getKey() + "--->" + entry.getValue());
			if (entry.getKey() == robotClient.player.getSeatNum()) {
				if (entry.getValue() != null) {
					mybet = entry.getValue();
				}
			} else {
				Long oneBet = entry.getValue();
				if (oneBet != null && oneBet > maxBet) {
					maxBet = oneBet;
				}
			}
		}
		callNeed = maxBet - mybet;
		SystemLog.printlog("callNeed:" + callNeed + "maxBet:" + maxBet + "myBet:" + mybet);
		// 获取10到99随机数
		int randomNum = RandomNumUtil.getNextInt(2);
		// 一定概率fold
		if (randomNum < 40 && callNeed > 0) {
			fold(robotClient);
		} else if (randomNum < 80) {
			int bet = (int) callNeed;
			if (bet > 0) {
				// 可以跟注，跟注
				betChips(robotClient, bet);
			} else {
				// 可以check的情况下
				check(robotClient);
			}
		} else if (randomNum < 96) {
			// 一定概率加注2倍
			int bet = (int) callNeed;
			if (bet > 0) {
				bet = bet * 2;
				//若加注，则必须等于大盲注的整数倍
				int times=bet/(int)bigbet;
				bet=(int)bigbet*times;
				betChips(robotClient, bet);
			} else {
				// 6倍大盲
				bet = bet + robotClient.roomInfo.getBigBet() * 3;
				if (bet > maxBet) {
					bet = (int) maxBet;
				}
				betChips(robotClient, bet);
			}
		} else  {
			// 一定概率allin
			int bet = (int) robotClient.player.getBodyChips();
			betChips(robotClient, bet);
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
			SystemLog.printlog("chips > robotClient.player.getBodyChips()  getBodyChips:"
					+ robotClient.player.getBodyChips() + "chips" + chips);
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
