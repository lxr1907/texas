package com.lxrtalk.texas.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.annotations.Expose;

import com.lxrtalk.texas.texas.Room;

/**
 * 私有房间信息（包含自己的手牌）
 * 
 * @author Ming
 *
 */
public class PrivateRoom implements Serializable {

	private static final long serialVersionUID = 1L;
	@Expose
	private int[] handPokers;
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
	 * 操作超时时间，单位毫秒（玩家在规定时间内没有完成操作，则系统自动帮其弃牌）
	 */
	@Expose
	private int optTimeout = 10000;
	/**
	 * 大盲下注筹码
	 */
	@Expose
	private int bigBet;
	@Expose
	private int roundMaxBet;
	@Expose
	private int nextturn;
	@Expose
	private int dealer;

	/**
	 * 每轮第一个行动的玩家
	 */
	protected int roundturn = 0;

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
	 * 在一回合中，每个玩家下的注[座位号，本轮下注额]
	 */
	@Expose
	protected Map<Integer, Long> betRoundMap = new LinkedHashMap<>();

	public void setRoom(Room room) {
		setBetRoundMap(room.getBetMap());
		setBigBet(room.getBigBet());
		setIngamePlayers(room.getIngamePlayers());
		setWaitPlayers(room.getWaitPlayers());
		setNextturn(room.getNextturn());
		setCommunityCards(room.getCommunityCards());
		setBetAmount(room.getBetAmount());
		setOptTimeout(room.getOptTimeout());
		setRoundMaxBet(room.getRoundMaxBet());
	}


	public int getRoundMaxBet() {
		return roundMaxBet;
	}

	public void setRoundMaxBet(int roundMaxBet) {
		this.roundMaxBet = roundMaxBet;
	}
	public int getRoundturn() {
		return roundturn;
	}

	public void setRoundturn(int roundturn) {
		this.roundturn = roundturn;
	}

	public List<Integer> getCommunityCards() {
		return communityCards;
	}

	public void setCommunityCards(List<Integer> communityCards) {
		this.communityCards = communityCards;
	}

	public long getBetAmount() {
		return betAmount;
	}

	public void setBetAmount(long betAmount) {
		this.betAmount = betAmount;
	}

	public int getOptTimeout() {
		return optTimeout;
	}

	public void setOptTimeout(int optTimeout) {
		this.optTimeout = optTimeout;
	}

	public int getDealer() {
		return dealer;
	}

	public void setDealer(int dealer) {
		this.dealer = dealer;
	}

	public int getNextturn() {
		return nextturn;
	}

	public void setNextturn(int nextturn) {
		this.nextturn = nextturn;
	}

	public int getBigBet() {
		return bigBet;
	}

	public void setBigBet(int bigBet) {
		this.bigBet = bigBet;
	}

	public Map<Integer, Long> getBetRoundMap() {
		return betRoundMap;
	}

	public void setBetRoundMap(Map<Integer, Long> betRoundMap) {
		this.betRoundMap = betRoundMap;
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

	public int[] getHandPokers() {
		return handPokers;
	}

	public void setHandPokers(int[] handPokers) {
		this.handPokers = handPokers;
	}

}
