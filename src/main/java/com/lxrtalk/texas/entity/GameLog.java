package com.lxrtalk.texas.entity;

/**
 * 每盘结算时记录日志
 * 
 * @author Ming
 *
 */
public class GameLog extends BaseEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * id作为游戏编号，第多少局游戏
	 */

	/**
	 * 房间信息（普通场）
	 */
	private String roomType;
	/**
	 * 房间信息（级别）
	 */
	private int roomLevel;
	/**
	 * 庄家
	 */
	private String dealer;
	/**
	 * 小盲
	 */
	private String smallBet;
	/**
	 * 大盲
	 */
	private String bigBet;
	/**
	 * 第一圈（底牌圈）,翻牌圈信息,转牌圈信息,河牌圈信息,比牌圈（脚牌，亮牌）信息
	 * 
	 * <pre>
	 * <p>
	 * 发牌给 playerNickName[6h Tc]
	 * <p>
	 * playerNickName 操作 操作筹码 remark
	 * <p>
	 * playerNickName 跟注 1067 全下
	 * <p>
	 * playerNickName 跟注 1067
	 */
	private String roundInfo;

	/**
	 * 游戏开始时间
	 */
	private String startTime;
	/**
	 * 游戏结算时间
	 */
	private String endTime;
	/**
	 * 底池
	 */
	private long countBetpool;
	/**
	 * 奖池信息
	 * 
	 * <pre>
	 * <p>
	 * playerNickName 从 主底池中赢得 11742
	 * <p>
	 * playerNickName 从 额外底池中赢得 10667
	 */
	private String betpoolInfo;
	/**
	 * 抽成
	 */
	private long cut;

	/**
	 * 公共牌[7s Jh 5d 8h ..]
	 */
	private String communityCards;
	/**
	 * 房间中玩家的初始信息
	 * 
	 * <pre>
	 * <p>
	 * 第1号座位：playerNickName (9600筹码)
	 * <p>
	 * 第2号座位：playerNickName (8158筹码)
	 * <p>
	 * 第3号座位：playerNickName (18349筹码)
	 * <p>
	 * 第4号座位：playerNickName (22975筹码)
	 * <p>
	 * 第5号座位：playerNickName (15494筹码)
	 * <p>
	 * 第6号座位：playerNickName (4015筹码)
	 */
	private String playersInitInfo;
	/**
	 * 房间中玩家的最终信息
	 * 
	 * <pre>
	 * <p>
	 * 第1号座位：playerNickName (remark) 亮出底牌：[Js Kc],胜出（胜出所得筹码） 其获胜牌组：一对J
	 * <p>
	 * 第2号座位：playerNickName (小盲) 盖牌 早于 翻盘
	 * <p>
	 * 第3号座位：playerNickName (大盲) 盖牌 早于 翻盘
	 * <p>
	 * 第4号座位：playerNickName (remark) 亮出底牌：[2d Kd],败北 其获胜牌组：散牌K
	 * <p>
	 * 第5号座位：playerNickName (remark) 盖牌 早于 翻盘
	 * <p>
	 * 第6号座位：playerNickName (remark) 亮出底牌：[6h Tc],败北 其获胜牌组：一对10
	 */
	private String playersFinalInfo;
	/**
	 * 获胜玩家和对应所赢筹码信息
	 */
	private String winnersInfo;

	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	public int getRoomLevel() {
		return roomLevel;
	}

	public void setRoomLevel(int roomLevel) {
		this.roomLevel = roomLevel;
	}

	public String getDealer() {
		return dealer;
	}

	public void setDealer(String dealer) {
		this.dealer = dealer;
	}

	public String getSmallBet() {
		return smallBet;
	}

	public void setSmallBet(String smallBet) {
		this.smallBet = smallBet;
	}

	public String getBigBet() {
		return bigBet;
	}

	public void setBigBet(String bigBet) {
		this.bigBet = bigBet;
	}

	public String getRoundInfo() {
		return roundInfo;
	}

	public void setRoundInfo(String roundInfo) {
		this.roundInfo = roundInfo;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public long getCountBetpool() {
		return countBetpool;
	}

	public void setCountBetpool(long countBetpool) {
		this.countBetpool = countBetpool;
	}

	public String getBetpoolInfo() {
		return betpoolInfo;
	}

	public void setBetpoolInfo(String betpoolInfo) {
		this.betpoolInfo = betpoolInfo;
	}

	public long getCut() {
		return cut;
	}

	public void setCut(long cut) {
		this.cut = cut;
	}

	public String getCommunityCards() {
		return communityCards;
	}

	public void setCommunityCards(String communityCards) {
		this.communityCards = communityCards;
	}

	public String getPlayersInitInfo() {
		return playersInitInfo;
	}

	public void setPlayersInitInfo(String playersInitInfo) {
		this.playersInitInfo = playersInitInfo;
	}

	public String getPlayersFinalInfo() {
		return playersFinalInfo;
	}

	public void setPlayersFinalInfo(String playersFinalInfo) {
		this.playersFinalInfo = playersFinalInfo;
	}

	public String getWinnersInfo() {
		return winnersInfo;
	}

	public void setWinnersInfo(String winnersInfo) {
		this.winnersInfo = winnersInfo;
	}

}
