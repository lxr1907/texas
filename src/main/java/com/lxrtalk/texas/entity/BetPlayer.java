package com.lxrtalk.texas.entity;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

/**
 * 玩家下注信息
 * 
 * @author Ming
 *
 */
public class BetPlayer extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 座位号,从0开始算,没有座位-1
	 */
	@Expose
	private int seatNum = -1;

	/**
	 * 筹码(身上所携带的筹码，游戏中可以用来下注的筹码)
	 */
	@Expose
	private long bodyChips;
	/**
	 * 在一局中下注的次数
	 */
	@Expose
	private int betTimes;

	/**
	 * 下注筹码(本轮下注筹码)
	 */
	@Expose
	private int inChips;

	public int getBetTimes() {
		return betTimes;
	}

	public void setBetTimes(int betTimes) {
		this.betTimes = betTimes;
	}

	public int getSeatNum() {
		return seatNum;
	}

	public void setSeatNum(int seatNum) {
		this.seatNum = seatNum;
	}

	public long getBodyChips() {
		return bodyChips;
	}

	public void setBodyChips(long bodyChips) {
		this.bodyChips = bodyChips;
	}

	public int getInChips() {
		return inChips;
	}

	public void setInChips(int inChips) {
		this.inChips = inChips;
	}

}
