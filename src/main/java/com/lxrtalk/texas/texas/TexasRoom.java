package com.lxrtalk.texas.texas;

import com.google.gson.annotations.Expose;
import com.lxrtalk.texas.cardUtils.CardGroup;
import com.lxrtalk.texas.cardUtils.CardUtil;
import com.lxrtalk.texas.entity.*;
import com.lxrtalk.texas.service.GameLogService;
import com.lxrtalk.texas.service.PlayerService;
import com.lxrtalk.texas.service.SystemLogService;
import com.lxrtalk.texas.service.impl.LobbyServiceImpl;
import com.lxrtalk.texas.utils.SpringUtil;
import com.lxrtalk.texas.utils.dateTime.DateUtil;
import com.lxrtalk.texas.utils.serialize.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 房间实体
 */
@Component("texasRoom")
public class TexasRoom {
    @Autowired
    GameLogService gameLogService;
    @Autowired
    PlayerService playerService;
    //房间根据级别，固定参数配置
    @Expose
    TexasRoomConfig config;
    private static final Logger logger = LogManager.getLogger(TexasRoom.class);

    /**
     * 游戏日志
     */
    protected GameLog gameLog = new GameLog();
    /**
     * 玩家操作列表
     */
    protected List<PlayerOpt> opts = new ArrayList<PlayerOpt>();
    /**
     * 房间id
     */
    @Expose
    private int id;
    /**
     * 房间级别
     */
    @Expose
    private int level;
    /**
     * 房间类型，0德州，1三张牌
     */
    @Expose
    private int type;

    /**
     * D，最佳座位，庄家（座位号）
     */
    @Expose
    private int dealer;
    /**
     * 小盲玩家座位号
     */
    @Expose
    private int smallBetSeatNum;
    /**
     * 大盲玩家座位号
     */
    @Expose
    private int bigBetSeatNum;
    /**
     * 游戏状态（0，等待；1，游戏，2结算中）
     */
    @Expose
    private AtomicInteger gamestate = new AtomicInteger(0);
    /**
     * 房间状态（0，不可加入；1，可加入）
     */
    private volatile int roomstate = 1;
    /**
     * 房间中处于等待状态的玩家列表
     */
    @Expose
    private List<Player> waitPlayers = new CopyOnWriteArrayList<Player>();
    /**
     * 房间中处于游戏状态的玩家列表
     */
    @Expose
    protected List<Player> ingamePlayers = new CopyOnWriteArrayList<Player>();
    /**
     * 一局的整体随机牌组
     */
    protected ArrayList<Integer> cardList = CardGroup.getRandomCards();
    /**
     * 公共牌
     */
    @Expose
    protected List<Integer> communityCards = new ArrayList<Integer>();
    /**
     * 奖池,下注总额
     */
    @Expose
    protected long betAmount;
    /**
     * 每个玩家下的注，玩家和其本局游戏下注的总额
     */
    protected Map<Integer, Long> betMap = new LinkedHashMap<>();

    /**
     * 在一回合中，每个玩家下的注[座位号，本轮下注额]
     */
    @Expose
    protected Map<Integer, Long> betRoundMap = new LinkedHashMap<>();

    /**
     * 操作过的玩家列表
     */
    public List<Integer> donePlayerList = new ArrayList<Integer>();

    /**
     * 下一个行动的玩家id
     */
    @Expose
    protected volatile int nextTurn = 0;
    /**
     * 每轮第一个行动的玩家
     */
    protected volatile int roundTurn = 0;
    /**
     * 本轮游戏玩家下的最大注，第一轮为大盲的1倍，一共3种，1,2,4
     */
    protected int roundMaxBet;
    /**
     * 存放座位号的栈(空闲座位)
     */
    @Expose
    private Stack<Integer> freeSeatStack;

    /**
     * 房间中等待操作的计时器(一个房间中不允许同时生成多个计时器)
     */
    private Timer timer = new Timer();
    /**
     * 游戏中玩家成手牌列表
     */
    @Expose
    protected Map<Integer, List<Integer>> finalCardsMap = new HashMap<Integer, List<Integer>>();
    protected Map<String, List<String>> finalCardsReadMap = new HashMap<String, List<String>>();
    /**
     * 最后亮牌玩家手牌列表
     */
    @Expose
    protected Map<Integer, List<Integer>> handCardsMap = new HashMap<Integer, List<Integer>>();
    /**
     * 所有获胜玩家列表
     */
    @Expose
    protected Map<Integer, Long> winPlayersMap = new HashMap<Integer, Long>();

    /**
     * 开始游戏
     *
     * <pre>
     * 游戏状态变更为游戏中，等待中的玩家移动到游戏中列表
     */
    public void startGame() {
        if (this.getGamestate().compareAndSet(0, 1)) {
            // 游戏日志-玩家操作信息
            opts.clear();

            // 由于需要通知在结束阶段进入的玩家牌局信息
            // 因此延迟到下局开始清除上局信息
            finalCardsMap.clear();
            handCardsMap.clear();
            winPlayersMap.clear();

            // 记录玩家座位号
            for (Player p : getIngamePlayers()) {
                p.setFold(false);// 设定为未弃牌
            }
            // 更新下一个dealer
            TexasUtil.updateNextDealer(this);
            // 得到一副洗好的牌（随机卡组）
            this.setCardList(CardGroup.getRandomCards());
            // 确定大小盲主，并分配筹码到奖池
            // 最佳位置
            int dealer = getDealer();
            // 小盲注
            int smallBet = config.getSmallBet();
            // 大盲注
            int bigBet = config.getBigBet();
            roundMaxBet = config.getBigBet();
            // 小盲位置
            int smallBetSeat = TexasUtil.getNextSeatNum(dealer, this);
            // 当只有2个玩家时，dealer才是小盲
            if (getIngamePlayers().size() == 2) {
                smallBetSeat = dealer;
            }
            this.setSmallBetSeatNum(smallBetSeat);
            // 大盲位置
            int bigBetSeat = TexasUtil.getNextSeatNum(smallBetSeat, this);
            this.setBigBetSeatNum(bigBetSeat);
            // 小盲玩家
            Player smallBetPlayer = TexasUtil.getPlayerBySeatNum(smallBetSeat, getIngamePlayers());
            // 大盲玩家
            Player bigBetPlayer = TexasUtil.getPlayerBySeatNum(bigBetSeat, getIngamePlayers());
            // 小盲玩家下小盲注
            betchipIn(smallBetPlayer, smallBet, false);
            // 大盲玩家下大盲注
            betchipIn(bigBetPlayer, bigBet, false);
            // 更新下一个该操作的玩家
            nextTurn = TexasUtil.getNextSeatNum(bigBetSeat, this);
            // 更新下一轮该操作的玩家为小盲位置
            roundTurn = smallBetSeat;
            // 当只有2个玩家时，第一轮Dealer（同时也是小盲）先操作
            // 第二轮开始大盲先操作
            if (getIngamePlayers().size() == 2) {
                roundTurn = bigBetSeat;
            }
            // 分发手牌
            TexasUtil.assignHandPokerByRoom(this);
            // 通知房间中在游戏中的玩家此刻的房间(包含私有信息【公共牌+手牌】的房间)状态信息
            for (Player p : getIngamePlayers()) {
                PrivateRoom pRoom = new PrivateRoom();
                pRoom.setRoom(this);
                // 私有房间信息（手牌）
                pRoom.setHandPokers(p.getHandPokers());
                String msg = JsonUtils.toJson(pRoom, PrivateRoom.class);
                RetMsg retMsg = new RetMsg();
                retMsg.setC("onGameStart");
                retMsg.setState(1);
                retMsg.setMessage(msg);
                TexasUtil.sendMsgToOne(p, JsonUtils.toJson(retMsg, RetMsg.class));
            }
            startTimer(this);// 开始计时
            // 游戏日志
            gameLog.setStartTime(DateUtil.nowDatetime());
            String initInfo = JsonUtils.toJson(this.getIngamePlayers(), this.getIngamePlayers().getClass());
            gameLog.setPlayersInitInfo(initInfo);
            gameLog.setRoomLevel(this.getLevel());
            gameLog.setRoomType("普通场");//
            gameLog.setBigBet(JsonUtils.toJson(bigBetPlayer, Player.class));
            gameLog.setSmallBet(JsonUtils.toJson(smallBetPlayer, Player.class));
            gameLog.setDealer(
                    JsonUtils.toJson(TexasUtil.getPlayerBySeatNum(dealer, this.getIngamePlayers()), Player.class));

        }
    }

    /**
     * 结束游戏
     *
     * <pre>
     * 游戏状态变更为等待，游戏中的玩家移动到等待列表
     */
    public void endGame() {
        Date now = new Date();
        // 尝试更新游戏状态为2：结算中
        if (this.getGamestate().compareAndSet(1, 2)) {
            long cut = 0;// 本局游戏的系统抽成筹码
            logger.info("endGame begin");
            int allinCount = 0;
            // 统计allin玩家数
            for (Player p : getIngamePlayers()) {
                if (p.getBodyChips() == 0) {
                    allinCount++;
                }
            }
            // 如果公共牌没有发完，且allin人数大于等于2,则先发完公共牌
            if (communityCards.size() < 5 && allinCount >= 2) {
                logger.info("communityCards.size() < 5 && allinCount >= 2");
                // 发公共牌
                int assignCardCount = 5 - communityCards.size();
                TexasUtil.assignCommonCardByNum(this, assignCardCount);
            }
            int timeBetween = config.getRestBetweenGame();
            if (communityCards.size() == 5) {
                // 成手牌列表
                for (Player p : getIngamePlayers()) {
                    List<Integer> hankPoker = new ArrayList<Integer>();
                    List<Integer> hankPokerAndCommonCard = new ArrayList<Integer>();
                    hankPokerAndCommonCard.addAll(getCommunityCards());
                    for (int i = 0; i < p.getHandPokers().length; i++) {
                        hankPokerAndCommonCard.add(p.getHandPokers()[i]);
                        hankPoker.add(p.getHandPokers()[i]);
                    }
                    // 判断牌型并将牌型编号加在数组最后一位
                    List<Integer> maxCardsGroup = CardUtil.getMaxHand(hankPokerAndCommonCard);
                    // 加入成手牌列表
                    finalCardsMap.put(p.getSeatNum(), maxCardsGroup);
                    finalCardsReadMap.put(p.getUsername(), CardUtil.cardsToStringList(maxCardsGroup));
                    // 加入互相可见的手牌列表
                    handCardsMap.put(p.getSeatNum(), hankPoker);
                }
            }
            gameLog.setEndTime(DateUtil.nowDatetime());
            gameLog.setCommunityCards(communityCards.toString());
            gameLog.setPlayersFinalInfo(JsonUtils.toJson(finalCardsReadMap, finalCardsReadMap.getClass()));
            // 奖池列表
            List<BetPool> betPoolList = new ArrayList<BetPool>();
            // 计算betpool
            logger.info("sumBetPoolList begin");
            sumBetPoolList(betPoolList, betMap, ingamePlayers);
            // 对每个分池结算
            for (BetPool betpool : betPoolList) {
                // 单个分池中的获胜玩家列表
                List<Player> winPlayerList = new ArrayList<>();
                // 本分池的玩家列表
                List<Player> poolPlayers = betpool.getBetPlayerList();
                if (!finalCardsMap.isEmpty()) {
                    // 获取本分池获胜玩家
                    logger.info("compareCardsToWinList begin");
                    winPlayerList = compareCardsToWinList(poolPlayers, finalCardsMap);
                }
                // 没有则认为第一个获胜,若公共牌未发完结束游戏，存在该情况
                if (winPlayerList.isEmpty()) {
                    for (Player p : poolPlayers) {
                        if (p != null && !p.isFold()) {
                            winPlayerList.add(p);
                            break;
                        }
                    }
                }
                Long win = 0L;
                if (!winPlayerList.isEmpty()) {
                    // 本次分池获胜的玩家分筹码
                    win = (betpool.getBetSum() / winPlayerList.size());
                }
                for (Player p : winPlayerList) {
                    TexasUtil.changePlayerChips(p, win);
                    // 在上个分池中已经赢的筹码，需要合并计算，加入winPlayersMap
                    Long lastPoolWin = winPlayersMap.get(p.getSeatNum());
                    if (lastPoolWin != null) {
                        win = win + lastPoolWin;
                    }
                    winPlayersMap.put(p.getSeatNum(), win);
                    LobbyServiceImpl.updateRankList(p);
                    logger.info("winPlayersMap.put :" + p.getSeatNum() + " thisPoolWin:" + win + "poolplayerssize:"
                            + betpool.getBetPlayerList().size());
                }
            }

            // 发送结算消息给玩家
            String msg = JsonUtils.toJson(this, TexasRoom.class);
            RetMsg retMsg = new RetMsg();
            retMsg.setC("onGameEnd");
            retMsg.setState(1);
            retMsg.setMessage(msg);
            TexasUtil.sendMsgToPlayerByRoom(this, JsonUtils.toJson(retMsg, RetMsg.class));
            gameLogService.insertGameLog(gameLog);
            clearOneGameInfos();

            // 更新玩家筹码数到数据库
            logger.info("updatePlayer ingamePlayers begin size:" + ingamePlayers.size());
            for (Player p : getWaitPlayers()) {
                Player playerData = new Player();
                playerData.setId(p.getId());
                synchronized (p) {
                    // 当前筹码数等于桌上筹码+身上筹码
                    playerData.setChips(p.getChips() + p.getBodyChips());
                    playerService.updatePlayer(playerData);
                }
                logger.info(
                        "updatePlayer ingamePlayers begin p:" + p.getUsername() + " chips:" + playerData.getChips());
            }

            Date costEnd = new Date();
            long cost = costEnd.getTime() - now.getTime();
            if (cost > 500) {
                logger.error("endGame:" + " cost Millisecond" + cost);
            }
            // 尝试更新游戏状态为0：等待开始
            this.getGamestate().compareAndSet(2, 0);
            // 判断是否可以开始下一局
            checkStart(timeBetween);
        }
    }

    /**
     * 清理一局的信息
     */
    private void clearOneGameInfos() {
        // 清除本局状态信息
        gameLog = new GameLog();
        betMap.clear();
        // 清除betRoundMap
        betRoundMap.clear();
        // 每局开始最大下注为一个大盲
        roundMaxBet = config.getBigBet();
        // 清除手牌
        for (Player p : ingamePlayers) {
            p.setHandPokers(null);
        }
        // 清除已经操作的玩家列表
        donePlayerList.clear();
        // 清除总下注
        betAmount = 0;
        // 清除公共牌
        communityCards.clear();
        // 清除牌堆
        cardList.clear();
        // 将玩家都移入等待列表
        TexasUtil.movePlayers(getIngamePlayers(), getWaitPlayers());
    }

    /**
     * 计算奖池分池
     *
     * @param betPoolList
     * @param betMap
     * @param ingamePlayers
     */
    public void sumBetPoolList(List<BetPool> betPoolList, Map<Integer, Long> betMap, List<Player> ingamePlayers) {
        // 对map按照值排序
        betMap = TexasUtil.sortMapByValue(betMap);
        boolean complete = false;
        while (!complete) {
            complete = true;
            // 该分池总金额
            Long betSum = 0L;
            // 该分池单个金额
            Long thisBet = 0L;
            BetPool pool = new BetPool();
            for (Entry<Integer, Long> e : betMap.entrySet()) {
                // 发现不为0的下注，则继续新的分池
                if (e.getValue() != 0) {
                    complete = false;
                    if (thisBet == 0) {
                        thisBet = e.getValue();
                    }
                    betSum = betSum + thisBet;
                    if (e.getValue() - thisBet < 0) {
                        System.out.println("betMap计算错误！下注排序从小到大");
                    }
                    // 减去本轮分池单个金额
                    e.setValue(e.getValue() - thisBet);
                    // 加入betpool
                    Player p = TexasUtil.getPlayerBySeatNum(e.getKey(), ingamePlayers);
                    if (p != null) {
                        pool.getBetPlayerList().add(p);
                    }
                }
            }
            pool.setBetSum(betSum);
            if (pool.getBetSum() != 0) {
                betPoolList.add(pool);
            }
        }
        betMap.clear();
    }

    /**
     * 根据单个奖池玩家列表，最终成牌列表，计算获胜玩家列表
     *
     * @param poolPlayers
     * @param finalCardsMap
     * @return
     */
    public List<Player> compareCardsToWinList(List<Player> poolPlayers, Map<Integer, List<Integer>> finalCardsMap) {
        List<Player> winPlayerList = new ArrayList<>();
        List<Integer> listold = null;
        if (finalCardsMap.size() == 0) {
            logger.info("finalCardsMap.size()==0 can not compareCardsToWinList");
            return null;
        }
        for (Entry<Integer, List<Integer>> e : finalCardsMap.entrySet()) {
            // 判断是否在本分池内
            boolean inThisPool = false;
            for (Player p : poolPlayers) {
                if (p != null && !p.isFold() && e.getKey() == p.getSeatNum()) {
                    inThisPool = true;
                    break;
                }
            }
            // 不在分池内
            if (!inThisPool) {
                continue;
            }
            logger.info("compareCardsToWinList inThisPool:" + inThisPool);
            // 旧卡组为空，则加入
            if (listold == null) {
                listold = e.getValue();
                logger.info("getPlayerBySeatNum begin seatNum:" + e.getKey() + " poolPlayers:"
                        + JsonUtils.toJson(poolPlayers, poolPlayers.getClass()));
                Player wp = TexasUtil.getPlayerBySeatNum(e.getKey(), poolPlayers);
                if (wp != null) {
                    winPlayerList.add(wp);
                } else {
                    logger.info("winPlayerList.add e.getKey():" + e.getKey() + "wp not in poolPlayers");
                }
                logger.info("winPlayerList.add e.getKey():" + e.getKey());
            } else {
                List<Integer> listNew = e.getValue();
                // 比较新旧卡组，大或相等则清空winPlayerList，加入新卡组的玩家
                logger.info("compareCardsToWinList CardUtil.compareValue listNew:" + listNew + " listold" + listold);
                int result = CardUtil.compareHands(e.getValue(), listold);
                logger.info("compareCardsToWinList CardUtil.compareValue result:" + result);
                if (result == 1) {
                    winPlayerList.clear();
                    winPlayerList.add(TexasUtil.getPlayerBySeatNum(e.getKey(), poolPlayers));
                    listold = listNew;
                } else if (result == 0) {
                    winPlayerList.add(TexasUtil.getPlayerBySeatNum(e.getKey(), poolPlayers));
                }
            }
        }
        return winPlayerList;
    }

    /**
     * 判断游戏是否可以开始
     */
    public synchronized void checkStart(int milsecond) {
        try {
            Thread.sleep(milsecond);
        } catch (InterruptedException e) {
            logger.error("", e);
        }
        // 不在等待开始，则返回
        if (!(getGamestate().get() == 0)) {
            return;
        }
        // 重新补筹码
        for (Player p : getWaitPlayers()) {
            //身上的筹码少于2倍大盲注则补充
            if (p.getBodyChips() < config.getBigBet() * 2L) {
                assignChipsForInRoom(p);
            }
        }
        // 总筹码不足一个大盲注的不能进行游戏，踢出房间
        getWaitPlayers().parallelStream().filter(p -> p.getBodyChips() <= config.getBigBet())
                .forEach(TexasUtil::outRoom);
        // 转移等待列表的玩家进入游戏中玩家列表
        TexasUtil.moveMaxPlayers(this.getWaitPlayers(), this.getIngamePlayers(), config.getMaxPlayers());
        // 玩家数大于等于最小开始游戏玩家，则开始
        if (getIngamePlayers().size() >= config.getMinPlayers()) {
            startGame();
        } else {
            checkStart(3000);
        }
    }

    /**
     * 为进入房间的用户分配筹码
     *
     * @param player
     */
    public void assignChipsForInRoom(Player player) {
        long takeChip = config.getMaxChips();
        // 如果玩家的所剩筹码不超过房间规定的最大带入筹码，则该玩家筹码全部带入
        if (player.getChips() < takeChip) {
            takeChip = player.getChips();
        }
        synchronized (player) {
            player.setChips(player.getChips() - takeChip);
            player.setBodyChips(takeChip);
        }
    }

    /**
     * 为用户初始化为房间最大带入
     *
     * @param player
     */
    public void assignChipsToRoomMax(Player player) {
        // 玩家的钱多退少补，使其等于房间最大带入
        long takeChip = config.getMaxChips() - player.getBodyChips();
        // 如果玩家的所剩筹码不超过需要补足的，则带入所有
        if (player.getChips() < takeChip) {
            takeChip = player.getChips();
        }
        synchronized (player) {
            player.setChips(player.getChips() - takeChip);
            player.setBodyChips(takeChip + player.getBodyChips());
        }
    }

    /**
     * 为退出房间的用户分配筹码,并入库
     *
     * @param player
     */
    public void assignChipsForOutRoom(Player player) {
        synchronized (player) {
            // 当前筹码数等于桌上筹码+身上筹码
            player.setChips(player.getChips() + player.getBodyChips());
            player.setBodyChips(0);
            // 玩家筹码信息入库
            PlayerService playerService = (PlayerService) SpringUtil.getBean("playerService");
            playerService.updatePlayer(player);
        }
    }

    /**
     * 玩家弃牌
     *
     * @param player
     */
    public void fold(Player player) {
        // 房间状态游戏中
        if (player.getRoom().getGamestate().get() != 1) {
            return;
        }
        if (player.getSeatNum() != nextTurn) {
            return;
        }
        try {
            // 检测到玩家操作，计时取消
            cancelTimer();
            synchronized (player) {
                // 弃牌
                player.setFold(true);
            }
            // 发送弃牌消息给玩家
            String msg = JsonUtils.toJson(player, Player.class);
            RetMsg retMsg = new RetMsg();
            retMsg.setC("onPlayerFold");
            retMsg.setState(1);
            retMsg.setMessage(msg);
            TexasUtil.sendMsgToPlayerByRoom(this, JsonUtils.toJson(retMsg, RetMsg.class));
            // 将玩家移动到等待列表
            TexasUtil.removeIngamePlayer(player);
            int index = donePlayerList.indexOf(player.getSeatNum());
            // 玩家在donePlayerList，则移除
            if (index != -1) {
                donePlayerList.remove(index);
            }
            getWaitPlayers().add(player);
        } catch (Exception e) {
            logger.error("", e);
        }
        // 判断下一步是否round结束，endgame或下个玩家操作nextturn
        endRoundOrNextTurn();
        // 记录日志
        PlayerOpt opt = new PlayerOpt();
        opt.setOptTime(DateUtil.nowDatetime());
        opt.setOptType("fold");// 操作类型（跟注、加注、弃牌、全下）
        opt.setRemark("");// 备注
        opt.setPlayerId(player.getId());
        opt.setSeatNum(player.getSeatNum());
        opts.add(opt);
    }

    /**
     * 判断下一步是否round结束，endgame或下个玩家操作nextturn
     */
    public void endRoundOrNextTurn() {
        // 判断是否可以结束本轮
        boolean roundEnd = checkRoundEnd();
        if (!roundEnd) {
            // 更新nextturn
            TexasUtil.updateNextTurn(this);
            // 发送轮到某玩家操作的消息
            sendNextTurnMessage();
        }
    }

    public void check(Player player, boolean playerOpt) {
        // 房间状态游戏中
        if (player.getRoom().getGamestate().get() != 1) {
            return;
        }
        // 是否轮到该玩家操作
        if (player.getSeatNum() != nextTurn) {
            return;
        }
        // 玩家此次操作之前的本轮下注额
        long oldBetThisRound = 0;
        if (getBetRoundMap().get(player.getSeatNum()) != null) {
            oldBetThisRound = getBetRoundMap().get(player.getSeatNum());
        }
        // 小于最大下注，不能check
        if (oldBetThisRound < getRoundMaxBet()) {
            logger.info("can not check, bet:" + oldBetThisRound + " getRoundMaxBet:" + getRoundMaxBet());
            return;
        }
        try {
            // 检测到玩家操作，计时取消
            cancelTimer();
            // 记录当前轮已经操作过的玩家
            if (!donePlayerList.contains(player.getSeatNum())) {
                donePlayerList.add(player.getSeatNum());
            }
            // 发送过牌消息给玩家
            String msg = JsonUtils.toJson(player, Player.class);
            RetMsg retMsg = new RetMsg();
            retMsg.setC("onPlayerCheck");
            retMsg.setState(1);
            retMsg.setMessage(msg);
            TexasUtil.sendMsgToPlayerByRoom(this, JsonUtils.toJson(retMsg, RetMsg.class));

        } catch (Exception e) {
            logger.error("", e);
        }
        // 判断下一步是否round结束，endgame或下个玩家操作nextturn
        endRoundOrNextTurn();
    }

    /**
     * 下注
     *
     * @param player 下注的玩家
     * @param chip   下注的筹码
     */
    public boolean betchipIn(Player player, int chip, boolean playerOpt) {
        if (player == null) {
            return false;
        }
        if (playerOpt && player.getSeatNum() != nextTurn) {
            return false;
        }
        TexasRoom thisTexasRoom = player.getRoom();

        PlayerOpt opt = new PlayerOpt();
        opt.setOptChips(chip);
        opt.setOptTime(DateUtil.nowDatetime());
        opt.setOptType("");// 操作类型（跟注、加注、弃牌、全下）
        // 第几轮
        int rd = Math.abs(thisTexasRoom.getCommunityCards().size() - 1);
        opt.setRound(rd);
        opt.setRemark("");// 备注
        opt.setPlayerId(player.getId());
        opt.setSeatNum(player.getSeatNum());
        opts.add(opt);

        // 玩家此次操作之前的本轮下注额
        long oldBetThisRound = 0;
        if (thisTexasRoom.getBetRoundMap().get(player.getSeatNum()) != null) {
            oldBetThisRound = thisTexasRoom.getBetRoundMap().get(player.getSeatNum());
        }
        // 无效下注额,1筹码不足
        if (chip <= 0 || chip > player.getBodyChips()) {
            logger.error("betchipIn error not enough chips:" + chip + " getBodyChips():" + player.getBodyChips());
            return false;
        }
        if (playerOpt) {
            // 2.在没有allin的情况下，如果不是跟注，则下注必须是大盲的整数倍
            if (chip < player.getBodyChips()) {
                //2.1跟注-- 不能小于之前下注,否则强制增加到跟注筹码，不够则allin
                if ((chip + oldBetThisRound) < thisTexasRoom.getRoundMaxBet()) {
                    logger.error("betchipIn error < getRoundMaxBet() chip:" + chip + "oldBetThisRound:"
                            + oldBetThisRound + " max:" + thisTexasRoom.getRoundMaxBet());
                    if (thisTexasRoom.getRoundMaxBet() - oldBetThisRound < player.getBodyChips()) {
                        chip = (int) (thisTexasRoom.getRoundMaxBet() - oldBetThisRound);
                    } else {
                        chip = (int) player.getBodyChips();
                    }
                }
                //2.2加注，反加--
                if ((chip + oldBetThisRound) != thisTexasRoom.getRoundMaxBet()) {
                    // 本轮已经下注+当前加注-本轮最大下注，必须=大盲注的整数倍
                    if ((chip + oldBetThisRound - thisTexasRoom.getRoundMaxBet()) % config.getBigBet() != 0) {
                        logger.error("betchipIn error % bigbet != 0:" + chip + "oldBetThisRound:" + oldBetThisRound
                                + ",max:" + thisTexasRoom.getRoundMaxBet());
                        return false;
                    }
                }
            }

        }
        try {
            if (playerOpt) {
                // 检测到玩家操作，计时取消
                cancelTimer();
            }
            // 设置本轮最大加注
            if ((int) (chip + oldBetThisRound) > thisTexasRoom.getRoundMaxBet()) {
                thisTexasRoom.setRoundMaxBet((int) (chip + oldBetThisRound));
                // 加注额大于之前，则所有玩家重新加注
                donePlayerList.clear();
            }
            // 总奖池
            thisTexasRoom.setBetAmount(getBetAmount() + chip);
            // 记录当前轮已经操作过的玩家
            if (!thisTexasRoom.donePlayerList.contains(player.getSeatNum()) && playerOpt) {
                thisTexasRoom.donePlayerList.add(player.getSeatNum());
            }
            // 刷新下注列表
            Long beforeBet = 0L;
            if (thisTexasRoom.getBetMap().get(player.getSeatNum()) != null) {
                beforeBet = thisTexasRoom.getBetMap().get(player.getSeatNum());
            }
            // 加入玩家总下注map
            thisTexasRoom.getBetMap().put(player.getSeatNum(), beforeBet + chip);
            // 加入玩家本轮下注
            thisTexasRoom.getBetRoundMap().put(player.getSeatNum(), chip + oldBetThisRound);
            // 筹码入池，所带筹码扣除
            player.setBodyChips(player.getBodyChips() - chip);
        } catch (Exception e) {
            logger.error("", e);
        }
        if (playerOpt) {
            try {
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
                TexasUtil.sendMsgToPlayerByRoom(thisTexasRoom, msg);
            } catch (Exception e) {
                logger.error("", e);
            }
            // 判断下一步是否round结束，endgame或下个玩家操作nextturn
            endRoundOrNextTurn();
        }
        return true;
    }

    /**
     * 判断本轮是否可以结束
     */
    public boolean checkRoundEnd() {
        // 判断本轮是否可以结束
        boolean canEndRound = true;
        if (getIngamePlayers().size() == 1) {
            // 结算游戏
            logger.info("only one IngamePlayers endgame start");
            endGame();
            return true;
        }
        // 将已经allin的玩家加入已操作列表
        for (Player p : getIngamePlayers()) {
            if (p.getBodyChips() == 0) {
                donePlayerList.add(p.getSeatNum());
            }
        }
        // 所有人都已经操作过
        if (donePlayerList.size() >= getIngamePlayers().size()) {
            long betMax = 0L;// 以最大下注作为参照筹码
            for (int i = 0; i < getIngamePlayers().size(); i++) {
                Player p = getIngamePlayers().get(i);
                // 为betMax赋初始值
                if (getBetMap().get(p.getSeatNum()) != null && getBetMap().get(p.getSeatNum()) > betMax) {
                    betMax = getBetMap().get(p.getSeatNum());
                }
            }
            logger.info("checkRoundEnd betMax:" + betMax);
            for (int i = 0; i < getIngamePlayers().size(); i++) {
                Player p = getIngamePlayers().get(i);
                // 已经弃牌的排除在外
                if (p == null || p.isFold()) {
                    continue;
                }
                if (getBetMap().get(p.getSeatNum()) == null) {
                    // 存在没有下注的玩家
                    logger.info("checkRoundEnd no bet seatNum:" + p.getSeatNum());
                    canEndRound = false;
                    break;
                }
                // 没有allin的玩家中
                if (p.getBodyChips() > 0) {
                    // 没有弃牌的玩家中，存在下注额度小于betMax，则本轮不能结束
                    if (betMax > getBetMap().get(p.getSeatNum())) {
                        logger.info("not allin bet<maxbet seatNum:" + p.getSeatNum() + " bet "
                                + getBetMap().get(p.getSeatNum()) + " betMax:" + betMax);
                        canEndRound = false;
                        break;
                    }
                }
            }
        } else {
            logger.info("checkRoundEnd getDonePlayerList().size() < getIngamePlayers().size()");
            canEndRound = false;
        }

        // 如果下一个可以操作的玩家无法更新，游戏结束
        int turn = TexasUtil.getNextSeatNum(nextTurn, this);
        if (turn == nextTurn) {
            canEndRound = true;
        }
        if (canEndRound) {
            logger.info("canEndRound = true");
            // 第二轮最低加注设为0
            setRoundMaxBet(0);
            // 开始新的一轮
            // 清除玩家本轮下注
            betRoundMap.clear();
            // 清空操作过的玩家列表
            donePlayerList.clear();
            // 已经无法操作的玩家加入DonePlayerList
            for (int i = 0; i < getIngamePlayers().size(); i++) {
                Player p = getIngamePlayers().get(i);
                if (p != null && p.getBodyChips() <= 0) {
                    donePlayerList.add(p.getSeatNum());
                }
            }
            // 如果公共牌等于5张,且可操作玩家数小于总玩家数
            if (communityCards.size() < 5 && donePlayerList.size() < getIngamePlayers().size()) {
                // 更新nextturn为每轮第一个人
                Player roundturnp = TexasUtil.getPlayerBySeatNum(roundTurn, getIngamePlayers());
                if (roundturnp == null || roundturnp.isFold() || roundturnp.getBodyChips() == 0) {
                    roundTurn = TexasUtil.getNextSeatNum(roundTurn, this);
                }
                setNextTurn(roundTurn);
                // 发送轮到某玩家操作的消息
                sendNextTurnMessage();
                // 发公共牌
                int assignCardCount = communityCards.size() == 0 ? 3 : 1;
                TexasUtil.assignCommonCardByNum(this, assignCardCount);

            } else {
                // 结算游戏
                endGame();
            }
        }
        return canEndRound;
    }

    private TimerTask timerTask;

    /**
     * 开始计时
     */
    public void startTimer(TexasRoom texasRoom) {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        timer.purge();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    // 弃牌or check
                    Player p = TexasUtil.getPlayerBySeatNum(getNextTurn(), getIngamePlayers());
                    if (p != null) {
                        // 帮其执行弃牌操作
                        logger.info(p.getUsername() + " time is up,fold");
                        fold(p);
                    } else {
                        if (texasRoom.getGamestate().get() == 1) {
                            // 更新本应操作的玩家
                            TexasUtil.updateNextTurn(texasRoom);
                            // 发送轮到某玩家操作的消息
                            sendNextTurnMessage();
                        }
                    }
                } catch (Exception e) {
                    logger.error("", e);
                    SystemLogService syslogService = (SystemLogService) SpringUtil.getBean("systemLogService");
                    SystemLogEntity entity = new SystemLogEntity();
                    entity.setType("roomTimer");
                    entity.setOperation("roomTimer");
                    entity.setContent(e.getCause() + e.getStackTrace().toString());
                    entity.setDatetime(DateUtil.nowDatetime());
                    syslogService.insertSystemLog(entity);
                }
            }
        };
        // 考虑到网络传输延时，后台计时器多给与一个500毫秒的缓冲时间
        timer.schedule(timerTask, config.getOptTimeout() + 500);
    }

    /**
     * 发送轮到某玩家操作的消息
     */
    public void sendNextTurnMessage() {
        String message = getNextTurn() + "";
        RetMsg retMsg = new RetMsg();
        // 告诉前台轮到某个玩家操作了
        retMsg.setC("onPlayerTurn");
        retMsg.setState(1);
        retMsg.setMessage(message);
        String msg = JsonUtils.toJson(retMsg, RetMsg.class);
        // 轮到下一家操作，并开始计时
        startTimer(this);
        TexasUtil.sendMsgToPlayerByRoom(this, msg);
    }

    /**
     * 判断是否可以结束游戏
     */
    public boolean checkEnd() {
        int playerCount = 0;
        for (Player p : getIngamePlayers()) {
            if (!p.isFold() && p.getBodyChips() != 0) {
                playerCount++;
            }
        }
        if (playerCount < 2) {
            this.endGame();
            return true;
        }
        logger.info(playerCount + "断线后人数大于1");
        return false;
    }

    // ThreeCardRoom房间中实现
    public void loseCompareCards(Player player) {
    }

    /**
     * 取消计时
     */
    public boolean cancelTimer() {
        boolean result = false;
        if (timerTask != null) {
            result = timerTask.cancel();
            timerTask = null;
        }
        return result;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDealer() {
        return dealer;
    }

    public void setDealer(int dealer) {
        this.dealer = dealer;
    }

    public AtomicInteger getGamestate() {
        return gamestate;
    }

    public void setGamestate(AtomicInteger gamestate) {
        this.gamestate = gamestate;
    }

    public int getRoomstate() {
        return roomstate;
    }

    public void setRoomstate(int roomstate) {
        this.roomstate = roomstate;
    }

    public List<Player> getWaitPlayers() {
        return waitPlayers;
    }

    public void setWaitPlayers(List<Player> waitPlayers) {
        this.waitPlayers = waitPlayers;
    }

    public List<Player> getIngamePlayers() {
        return ingamePlayers;
    }

    public void setIngamePlayers(List<Player> ingamePlayers) {
        this.ingamePlayers = ingamePlayers;
    }

    public ArrayList<Integer> getCardList() {
        return cardList;
    }

    public void setCardList(ArrayList<Integer> cardList) {
        this.cardList = cardList;
    }

    public List<Integer> getCommunityCards() {
        return communityCards;
    }

    public void setCommunityCards(List<Integer> communityCards) {
        this.communityCards = communityCards;
    }

    public int getNextTurn() {
        return nextTurn;
    }

    public void setNextTurn(int nextTurn) {
        this.nextTurn = nextTurn;
    }

    public Stack<Integer> getFreeSeatStack() {
        return freeSeatStack;
    }

    public void setFreeSeatStack(Stack<Integer> freeSeatStack) {
        this.freeSeatStack = freeSeatStack;
    }

    public long getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(long betAmount) {
        this.betAmount = betAmount;
    }

    public Map<Integer, Long> getBetRoundMap() {
        return betRoundMap;
    }

    public void setBetRoundMap(Map<Integer, Long> betRoundMap) {
        this.betRoundMap = betRoundMap;
    }

    public Map<Integer, Long> getWinPlayersMap() {
        return winPlayersMap;
    }

    public void setWinPlayersMap(Map<Integer, Long> winPlayersMap) {
        this.winPlayersMap = winPlayersMap;
    }

    public int getSmallBetSeatNum() {
        return smallBetSeatNum;
    }

    public void setSmallBetSeatNum(int smallBetSeatNum) {
        this.smallBetSeatNum = smallBetSeatNum;
    }

    public int getBigBetSeatNum() {
        return bigBetSeatNum;
    }

    public void setBigBetSeatNum(int bigBetSeatNum) {
        this.bigBetSeatNum = bigBetSeatNum;
    }

    public Map<Integer, List<Integer>> getFinalCardsMap() {
        return finalCardsMap;
    }

    public void setFinalCardsMap(Map<Integer, List<Integer>> finalCardsMap) {
        this.finalCardsMap = finalCardsMap;
    }

    public int getRoundMaxBet() {
        return roundMaxBet;
    }

    public void setRoundMaxBet(int roundMaxBet) {
        this.roundMaxBet = roundMaxBet;
    }

    public Map<Integer, Long> getBetMap() {
        return betMap;
    }

    public void setBetMap(Map<Integer, Long> betMap) {
        this.betMap = betMap;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public TexasRoomConfig getConfig() {
        return config;
    }

    public void setConfig(TexasRoomConfig config) {
        this.config = config;
    }
}
