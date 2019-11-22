package yuelj.entity;

import com.google.gson.annotations.Expose;

/**
 * 玩家操作
 * @author Ming
 *
 */
public class PlayerOpt {

	/**
	 * 玩家ID
	 */
	@Expose
	private String playerId;
	/**
	 * 操作类型
	 */
	@Expose
	private String optType;
	/**
	 * 操作筹码
	 */
	@Expose
	private int optChips;
	/**
	 * 第几轮
	 */
	@Expose
	private int round;
	/**
	 * 备注
	 */
	@Expose
	private String remark;
	/**
	 * 操作时间
	 */
	@Expose
	private String optTime;
	/**
	 * 玩家座位号
	 */
	@Expose
	private int seatNum;

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public int getOptChips() {
		return optChips;
	}

	public void setOptChips(int optChips) {
		this.optChips = optChips;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOptTime() {
		return optTime;
	}

	public void setOptTime(String optTime) {
		this.optTime = optTime;
	}

	public int getSeatNum() {
		return seatNum;
	}

	public void setSeatNum(int seatNum) {
		this.seatNum = seatNum;
	}

}
