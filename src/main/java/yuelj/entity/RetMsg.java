package yuelj.entity;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class RetMsg implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 类型 
	 * onPlayerSit 玩家入局
	 * onPlayerUp 玩家离开
	 * onGameStart 游戏开始
	 * onGameEnd 游戏结束
	 * onPlayerMove 其他玩家操作
	 * onMessage 其他玩家发消息
	 * 
	 */
	@Expose
	private String c;
	/**
	 * 状态
	 * 0失败
	 * 1成功
	 */
	@Expose
	private int state;
	/**
	 * 消息
	 */
	@Expose
	private String message;
	
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
