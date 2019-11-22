package yuelj.texas.robot;

import java.util.Date;

import com.google.gson.annotations.Expose;

import yuelj.entity.BetPlayer;

public class RobotPlayer extends BetPlayer {
	private static final long serialVersionUID = 8177609408282689534L;
	/**
	 * 账号
	 */
	@Expose
	private String username;

	/**
	 * 要加入的房间级别
	 */
	@Expose
	private String level;

	/**
	 * 用户密码
	 */
	@Expose
	private String userpwd;
	/**
	 * 从ConcurrentLinkedQueue<Integer> robotAccount拿出的id
	 */
	private int robotAccountId;
	/**
	 * 机器人开始上线时间
	 */
	private Date robotStart;

	/**
	 * 是否已经弃牌
	 */
	@Expose
	private boolean isFold = true;
	/**
	 * 手牌
	 */
	private int[] handPokers;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	public boolean isFold() {
		return isFold;
	}

	public void setFold(boolean isFold) {
		this.isFold = isFold;
	}

	public int[] getHandPokers() {
		return handPokers;
	}

	public void setHandPokers(int[] handPokers) {
		this.handPokers = handPokers;
	}

	public Date getRobotStart() {
		return robotStart;
	}

	public void setRobotStart(Date robotStart) {
		this.robotStart = robotStart;
	}

	public int getRobotAccountId() {
		return robotAccountId;
	}

	public void setRobotAccountId(int robotAccountId) {
		this.robotAccountId = robotAccountId;
	}

	public String getUserpwd() {
		return userpwd;
	}

	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}
