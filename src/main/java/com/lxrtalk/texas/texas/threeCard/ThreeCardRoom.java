package com.lxrtalk.texas.texas.threeCard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import com.lxrtalk.texas.cardUtils.CardGroup;
import com.lxrtalk.texas.entity.BetPlayer;
import com.lxrtalk.texas.entity.GameLog;
import com.lxrtalk.texas.entity.Player;
import com.lxrtalk.texas.entity.PlayerOpt;
import com.lxrtalk.texas.entity.PlayerPrivate;
import com.lxrtalk.texas.entity.PrivateRoom;
import com.lxrtalk.texas.entity.RetMsg;
import com.lxrtalk.texas.service.GameLogService;
import com.lxrtalk.texas.service.PlayerService;
import com.lxrtalk.texas.service.impl.LobbyServiceImpl;
import com.lxrtalk.texas.texas.BetPool;
import com.lxrtalk.texas.texas.Room;
import com.lxrtalk.texas.texas.TexasUtil;
import com.lxrtalk.texas.utils.SpringUtil;
import com.lxrtalk.texas.utils.dateTime.DateUtil;
import com.lxrtalk.texas.utils.serialize.JsonUtils;

public class ThreeCardRoom extends Room {
	private static Logger logger = LogManager.getLogger(ThreeCardRoom.class);
	/**
	 * 初始锅底，回收的筹码
	 */
	@Expose
	private int tableFeeBet;
	/**
	 * 2倍大下注，例如：5,10,20中的20
	 */
	@Expose
	private int doubleBigBet;
	/**
	 * 小暗注
	 */
	@Expose
	private int smallBlindBet;
	/**
	 * 大暗注
	 */
	@Expose
	private int bigBlindBet;
	/**
	 * 2倍大暗注
	 */
	@Expose
	private int doubleBigBlindBet;
	/**
	 * 在结束前不发送的手牌
	 */
	protected Map<Integer, List<Integer>> handCardsMapPrivate = new HashMap<Integer, List<Integer>>();

	/**
	 * 
	 * 开始游戏
	 * 
	 * <pre>
	 * 游戏状态变更为游戏中，等待中的玩家移动到游戏中列表
	 * 
	 * @param room
	 */
	@Override
	public void startGame() {
		// 本轮最大下注初始为1倍下注基数
		roundMaxBet = 1;
		// 总筹码不足500的不能进行游戏，踢出房间
		getWaitPlayers().parallelStream().filter(p -> p.getBodyChips() < 500).forEach(p -> {
			TexasUtil.outRoom(p.getSession(), "", true);
		});
		if (getWaitPlayers().size() < this.getMinPlayers()) {
			return;
		}
		if (this.getGamestate().compareAndSet(0, 1)) {
			// 游戏日志-玩家操作信息
			opts = new ArrayList<PlayerOpt>();
			// 由于需要通知在结束阶段进入的玩家牌局信息
			handCardsMap.clear();
			winPlayersMap.clear();
			for (Player p : getWaitPlayers()) {
				// 清除下注次数
				p.setBetTimes(0);
				// 重新补筹码
				if (p.getBodyChips() != this.getMaxChips()) {
					assignChipsToRoomMax(p);
				}
			}
			// 转移等待列表的玩家进入游戏中玩家列表
			TexasUtil.movePlayers(this.getWaitPlayers(), this.getIngamePlayers());
			// 记录玩家座位号
			for (Player p : getIngamePlayers()) {
				// 设定为未弃牌
				p.setFold(false);
				// 看牌标志重置
				p.setLook(false);
				// 所有玩家下锅底
				betchipIn(p, tableFeeBet, false);
			}
			// 得到一副洗好的牌（随机卡组）
			this.setCardList(CardGroup.getRandomCards());
			// 最佳位置
			int dealer = getDealer();
			// 更新下一个该操作的玩家
			nextturn = TexasUtil.getNextSeatNum(dealer, this, false);
			// 分发手牌
			ThreeCardsUtil.assignHandPokerByRoom(this);
			// 通知房间中在游戏中的玩家此刻的房间信息
			for (Player p : getIngamePlayers()) {
				PrivateRoom pRoom = new PrivateRoom();
				pRoom.setRoom(this);
				String msg = JsonUtils.toJson(pRoom, PrivateRoom.class);
				RetMsg retMsg = new RetMsg();
				retMsg.setC("onGameStart");
				retMsg.setState(1);
				retMsg.setMessage(msg);
				TexasUtil.sendMsgToOne(p, JsonUtils.toJson(retMsg, RetMsg.class));
			}
			startTimer(this);// 开始计时
			// 游戏日志
			this.gameLog = new GameLog();
			this.gameLog.setStartTime(DateUtil.nowDatetime());
			List<PlayerPrivate> listtype = new ArrayList<PlayerPrivate>();
			for (Player p : this.getIngamePlayers()) {
				PlayerPrivate pp = new PlayerPrivate();
				pp.setId(p.getId());
				pp.setBodyChips(p.getBodyChips());
				pp.setChips(p.getChips());
				pp.setSeatNum(p.getSeatNum());
				pp.setHandPokers(p.getHandPokers());
				pp.setUsername(p.getUsername());
				listtype.add(pp);
			}
			String initInfo = JsonUtils.toJson(listtype, listtype.getClass());
			this.gameLog.setPlayersInitInfo(initInfo);
			this.gameLog.setRoomLevel(this.getLevel());
			this.gameLog.setRoomType("拼三张普通场");
			this.gameLog.setDealer(JsonUtils.toJson(TexasUtil.getPlayerBySeatNum(dealer, this.getIngamePlayers()), Player.class));
		}
	}

	/**
	 * 下注
	 * 
	 * @param player
	 *            下注的玩家
	 * @param chip
	 *            下注的筹码
	 * @param palyerOpt
	 *            认为玩家操作过true
	 */
	@Override
	public boolean betchipIn(Player player, int chip, boolean playerOpt) {
		// 房间状态游戏中
		if (player.getRoom().getGamestate().get() != 1) {
			return false;
		}
		Room thisRoom = player.getRoom();

		PlayerOpt opt = new PlayerOpt();
		opt.setOptChips(chip);
		opt.setOptTime(DateUtil.nowDatetime());
		opt.setOptType("betchipIn");// 操作类型（跟注、加注、弃牌、全下）
		opt.setRemark("");// 备注
		opt.setPlayerId(player.getId());
		opt.setSeatNum(player.getSeatNum());
		opts.add(opt);

		if (playerOpt && player.getSeatNum() != nextturn) {
			return false;
		}
		// 玩家是否看过牌
		boolean lookCard = true;
		synchronized (player) {
			lookCard = player.isLook();
		}
		// 玩家此次操作之前的本轮下注额
		long oldBetThisRound = 0;
		if (thisRoom.getBetRoundMap().get(player.getSeatNum()) != null) {
			oldBetThisRound = thisRoom.getBetRoundMap().get(player.getSeatNum());
		}
		if (playerOpt) {
			// 无效下注额,筹码不足
			if (chip <= 0 || chip > player.getBodyChips()) {
				logger.info("not enough chips:" + chip + " getBodyChips():" + player.getBodyChips());
				return false;
			}
			int betBase = 0;
			// 看牌下注基数和暗牌下注基数不同
			if (lookCard) {
				betBase = this.getSmallBet();
			} else {
				betBase = this.getSmallBlindBet();
			}
			// 不能小于之前下注倍数
			if (chip / betBase < thisRoom.getRoundMaxBet()) {
				logger.info("< getRoundMaxBet() chip:" + chip + "oldBetThisRound:" + oldBetThisRound + " max:"
						+ thisRoom.getRoundMaxBet());
				return false;
			}
			// 下注必须是最小基数的整数倍
			if (chip % betBase != 0) {
				logger.info("% betBase != 0:" + chip + "betBase:" + betBase);
				return false;
			}

			// 设置本轮最大加注是基础加注的倍数
			if (chip / betBase > thisRoom.getRoundMaxBet()) {
				thisRoom.setRoundMaxBet(chip / betBase);
			}
		}
		if (playerOpt) {
			cancelTimer();// 检测到玩家操作，计时取消
		}
		// 总奖池
		thisRoom.setBetAmount(getBetAmount() + chip);
		// 刷新下注列表
		Long beforeBet = 0l;
		if (thisRoom.getBetMap().get(player.getSeatNum()) != null) {
			beforeBet = thisRoom.getBetMap().get(player.getSeatNum());
		}
		// 加入玩家总下注map
		thisRoom.getBetMap().put(player.getSeatNum(), beforeBet + chip);
		// 加入玩家本轮下注
		thisRoom.getBetRoundMap().put(player.getSeatNum(), chip + oldBetThisRound);
		// 筹码入池，所带筹码扣除
		player.setBodyChips(player.getBodyChips() - chip);

		if (playerOpt) {
			BetPlayer bp = new BetPlayer();
			bp.setBodyChips(player.getBodyChips());
			bp.setInChips(chip);
			bp.setSeatNum(player.getSeatNum());
			String message = JsonUtils.toJson(bp, BetPlayer.class);
			RetMsg retMsg = new RetMsg();
			retMsg.setC("onPlayerBet");// 告诉前台有玩家下注了
			retMsg.setState(1);
			retMsg.setMessage(message);
			String msg = JsonUtils.toJson(retMsg, RetMsg.class);
			// 通知房间中玩家有人下注了
			TexasUtil.sendMsgToPlayerByRoom(thisRoom, msg);
			// 判断下一步是否round结束，endgame或下个玩家操作nextturn
			endRoundOrNextTurn();
		}
		player.setBetTimes(player.getBetTimes() + 1);
		return true;
	}

	/**
	 * 玩家比牌下注
	 * 
	 * @param player
	 * @param chip
	 * @return
	 */
	public boolean betCompareCard(Player player, int chip) {
		// 房间状态游戏中
		if (player.getRoom().getGamestate().get() != 1) {
			return false;
		}
		Room thisRoom = player.getRoom();

		PlayerOpt opt = new PlayerOpt();
		opt.setOptChips(chip);
		opt.setOptTime(DateUtil.nowDatetime());
		opt.setOptType("betCompareCard");// 操作类型（跟注、加注、弃牌、比牌下注）
		opt.setRemark("");// 备注
		opt.setPlayerId(player.getId());
		opt.setSeatNum(player.getSeatNum());
		opts.add(opt);

		if (player.getSeatNum() != nextturn) {
			return false;
		}
		// 玩家是否看过牌
		boolean lookCard = true;
		synchronized (player) {
			lookCard = player.isLook();
		}
		// 玩家此次操作之前的本轮下注额
		long oldBetThisRound = 0;
		if (thisRoom.getBetRoundMap().get(player.getSeatNum()) != null) {
			oldBetThisRound = thisRoom.getBetRoundMap().get(player.getSeatNum());
		}

		// 无效下注额,筹码不足
		if (chip <= 0 || chip > player.getBodyChips()) {
			logger.info("not enough chips:" + chip + " getBodyChips():" + player.getBodyChips());
			return false;
		}
		// 检测到玩家操作，计时取消
		cancelTimer();
		boolean fold = false;
		synchronized (player) {
			// 判斷是否弃牌
			fold = player.isFold();
		}
		if (fold) {
			return false;
		}
		int betBase = 0;
		// 看牌下注基数和暗牌下注基数不同
		if (lookCard) {
			betBase = this.getSmallBet();
		} else {
			betBase = this.getSmallBlindBet();
		}
		// 设置本轮最大加注是基础加注的倍数
		if (chip / betBase > thisRoom.getRoundMaxBet()) {
			thisRoom.setRoundMaxBet(chip / betBase);
		}
		// 总奖池
		thisRoom.setBetAmount(getBetAmount() + chip);
		// 刷新下注列表
		Long beforeBet = 0l;
		if (thisRoom.getBetMap().get(player.getSeatNum()) != null) {
			beforeBet = thisRoom.getBetMap().get(player.getSeatNum());
		}
		// 加入玩家总下注map
		thisRoom.getBetMap().put(player.getSeatNum(), beforeBet + chip);
		// 加入玩家本轮下注
		thisRoom.getBetRoundMap().put(player.getSeatNum(), chip + oldBetThisRound);
		// 筹码入池，所带筹码扣除
		player.setBodyChips(player.getBodyChips() - chip);

		// 通知
		BetPlayer bp = new BetPlayer();
		bp.setBodyChips(player.getBodyChips());
		bp.setInChips(chip);
		bp.setSeatNum(player.getSeatNum());
		String message = JsonUtils.toJson(bp, BetPlayer.class);
		RetMsg retMsg = new RetMsg();
		retMsg.setC("onPlayerBet");// 告诉前台有玩家下注了
		retMsg.setState(1);
		retMsg.setMessage(message);
		String msg = JsonUtils.toJson(retMsg, RetMsg.class);
		// 通知房间中玩家有人下注了
		TexasUtil.sendMsgToPlayerByRoom(thisRoom, msg);
		// 下注次数加一
		player.setBetTimes(player.getBetTimes() + 1);
		return true;
	}

	/**
	 * 判断下一步是否round结束，endgame或下个玩家操作nextturn
	 */
	public void endRoundOrNextTurn() {
		// 判断是否可以结束本轮
		boolean roundEnd = checkRoundEnd();
		if (!roundEnd) {
			// 更新nextturn
			TexasUtil.updateNextTurn(this, false);
			// 发送轮到某玩家操作的消息
			sendNextTurnMessage();
		}
	}

	/**
	 * 判断本轮是否可以结束
	 */
	@Override
	public boolean checkRoundEnd() {
		// 判断本轮是否可以结束
		boolean canEndRound = false;
		if (getIngamePlayers().size() == 1) {
			// 结算游戏
			logger.info("threeCard only one IngamePlayers endgame start");
			endGame();
			return true;
		}
		// 如果下一个可以操作的玩家无法更新，游戏结束
		int turn = TexasUtil.getNextSeatNum(nextturn, this, false);
		if (turn == nextturn) {
			canEndRound = true;
		}
		if (canEndRound) {
			// 结算游戏
			endGame();
		}
		return canEndRound;
	}

	/**
	 * 结束游戏
	 * 
	 * <pre>
	 * 游戏状态变更为等待，游戏中的玩家移动到等待列表
	 * 
	 * @param room
	 * @param player
	 */
	@Override
	public void endGame() {
		// 尝试更新游戏状态为2：结算中
		if (this.getGamestate().compareAndSet(1, 2)) {
			long cut = 0;// 本局游戏的系统抽成筹码
			logger.info("endGame begin");
			// 手牌列表
			for (Player p : getIngamePlayers()) {
				List<Integer> hankPoker = new ArrayList<Integer>();
				for (int i = 0; i < p.getHandPokers().length; i++) {
					hankPoker.add(p.getHandPokers()[i]);
				}
				handCardsMapPrivate.put(p.getSeatNum(), hankPoker);
			}
			// 加入互相可见的手牌列表
			handCardsMap = handCardsMapPrivate;
			// 奖池
			BetPool betPool = new BetPool();
			long betSum = 0;
			for (Entry<Integer, Long> e : betMap.entrySet()) {
				if (e.getValue() != 0) {
					// 每个人下注，减去台费
					betSum = betSum + e.getValue() - tableFeeBet;
					cut = cut + tableFeeBet;
				}
			}
			betPool.setBetSum(betSum);
			// 获胜玩家列表
			Player winPlayer = null;
			// 最后剩余的一个玩家是胜者
			if (getIngamePlayers().size() > 0) {
				winPlayer = getIngamePlayers().get(0);
			}
			if (winPlayer != null) {
				Long win = 0l;
				// 获胜的玩家分筹码
				win = (Long) (betPool.getBetSum());
				// 给玩家加筹码
				TexasUtil.changePlayerChips(winPlayer, win);
				// 加入获胜列表
				winPlayersMap.put(winPlayer.getSeatNum(), win);
				// 更新下一局庄家为本次获胜玩家
				this.setDealer(winPlayer.getSeatNum());
				// 更新排行榜
				LobbyServiceImpl.updateRankList(winPlayer);
				logger.info("winPlayersMap.put :" + winPlayer.getSeatNum() + " thisPoolWin:" + win);

				// 游戏日志
				this.gameLog.setEndTime(DateUtil.nowDatetime());
				String finalInfo = JsonUtils.toJson(this.getIngamePlayers(), this.getIngamePlayers().getClass());
				this.gameLog.setPlayersFinalInfo(finalInfo);
				String comCards = JsonUtils.toJson(this.getCommunityCards(), this.getCommunityCards().getClass());
				this.gameLog.setCommunityCards(comCards);
				this.gameLog.setCountBetpool(this.getBetAmount());
				this.gameLog.setBetpoolInfo(JsonUtils.toJson(betPool, betPool.getClass()));
				this.gameLog.setCut(cut);
				Gson gson = new Gson();
				this.gameLog.setRoundInfo(gson.toJson(opts));
			}
			// 发送结算消息给玩家
			String msg = JsonUtils.toJson(this, Room.class);
			RetMsg retMsg = new RetMsg();
			retMsg.setC("onGameEnd");
			retMsg.setState(1);
			retMsg.setMessage(msg);
			TexasUtil.sendMsgToPlayerByRoom(this, JsonUtils.toJson(retMsg, RetMsg.class));
			// 清除本局状态信息
			betMap.clear();
			// 清除betRoundMap
			betRoundMap.clear();
			// 清除每轮最大加注
			roundMaxBet = 0;
			// 清除手牌
			for (Player p : ingamePlayers) {
				p.setHandPokers(null);
			}
			// 清除总下注
			betAmount = 0;
			// 清除公共牌
			communityCards.clear();
			// 清除牌堆
			cardList.clear();
			// 更新玩家筹码数到数据库
			PlayerService pservice = (PlayerService) SpringUtil.getBean("playerService");
			logger.info("updatePlayer ingamePlayers begin size:" + ingamePlayers.size());

			// 将玩家都移入等待列表
			TexasUtil.movePlayers(getIngamePlayers(), getWaitPlayers());
			for (Player p : getWaitPlayers()) {
				Player playerData = new Player();
				playerData.setId(p.getId());
				synchronized (p) {
					// 当前筹码数等于桌上筹码+身上筹码
					playerData.setChips(p.getChips() + p.getBodyChips());
					pservice.updatePlayer(playerData);
				}
				logger.info(
						"updatePlayer ingamePlayers begin p:" + p.getUsername() + " chips:" + playerData.getChips());
			}
			// 记录游戏日志
			GameLogService gameLogService = (GameLogService) SpringUtil.getBean("gameLogService");
			gameLogService.insertGameLog(this.gameLog);
			// 尝试更新游戏状态为0：等待开始
			this.getGamestate().compareAndSet(2, 0);
			try {
				// 等待一段时间
				Thread.sleep(getRestBetweenGame());
				// 判断是否可以开始下一局
				checkStart(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 玩家比牌失败
	 * 
	 * @param player
	 */
	public void loseCompareCards(Player player) {
		// 弃牌
		player.setFold(true);
		// 发送比牌输给玩家
		String msg = JsonUtils.toJson(player, Player.class);
		RetMsg retMsg = new RetMsg();
		retMsg.setC("onPlayerLoseCompare");
		retMsg.setState(1);
		retMsg.setMessage(msg);
		TexasUtil.sendMsgToPlayerByRoom(this, JsonUtils.toJson(retMsg, RetMsg.class));
		// 将玩家移动到等待列表
		TexasUtil.removeIngamePlayer(player);
		getWaitPlayers().add(player);

		// 加入互相可见的手牌列表
		handCardsMapPrivate.put(player.getSeatNum(),
				Arrays.stream(player.getHandPokers()).boxed().collect(Collectors.toList()));

		//日志记录
		PlayerOpt opt = new PlayerOpt();
		opt.setOptTime(DateUtil.nowDatetime());
		opt.setOptType("loseCompare");// 操作类型（跟注、加注、弃牌、比牌败）
		opt.setRemark("");// 备注
		opt.setPlayerId(player.getId());
		opt.setSeatNum(player.getSeatNum());
		opts.add(opt);
		// 判断下一步是否round结束，endgame或下个玩家操作nextturn
		endRoundOrNextTurn();
	}

	public int getSmallBlindBet() {
		return smallBlindBet;
	}

	public void setSmallBlindBet(int smallBlindBet) {
		this.smallBlindBet = smallBlindBet;
	}

	public int getBigBlindBet() {
		return bigBlindBet;
	}

	public void setBigBlindBet(int bigBlindBet) {
		this.bigBlindBet = bigBlindBet;
	}

	public int getDoubleBigBlindBet() {
		return doubleBigBlindBet;
	}

	public void setDoubleBigBlindBet(int doubleBigBlindBet) {
		this.doubleBigBlindBet = doubleBigBlindBet;
	}

	public int getDoubleBigBet() {
		return doubleBigBet;
	}

	public void setDoubleBigBet(int doubleBigBet) {
		this.doubleBigBet = doubleBigBet;
	}

	public int getTableFeeBet() {
		return tableFeeBet;
	}

	public void setTableFeeBet(int tableFeeBet) {
		this.tableFeeBet = tableFeeBet;
	}

	public Map<Integer, List<Integer>> getHandCardsMapPrivate() {
		return handCardsMapPrivate;
	}

	public void setHandCardsMapPrivate(Map<Integer, List<Integer>> handCardsMapPrivate) {
		this.handCardsMapPrivate = handCardsMapPrivate;
	}
}
