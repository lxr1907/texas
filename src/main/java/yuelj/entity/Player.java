package yuelj.entity;

import java.io.Serializable;

import javax.websocket.Session;

import com.google.gson.annotations.Expose;

import yuelj.texas.Room;

/**
 * 玩家实体
 * 
 * @author Ming
 *
 */
public class Player extends BetPlayer implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 账号
	 */
	@Expose
	private String username;
	/**
	 * 昵称
	 */
	@Expose
	private String nickname;
	/**
	 * 邮箱
	 */
	@Expose
	private String email;
	/**
	 * 手机
	 */
	@Expose
	private String phone;
	/**
	 * 用户密码
	 */
	@Expose(serialize = false, deserialize = true)
	private String userpwd;
	/**
	 * 用户Session
	 */
	@Expose(serialize = false, deserialize = false)
	private Session session;
	/**
	 * 头像
	 */
	@Expose
	private int pic;
	/**
	 * 筹码(用户所拥有的总筹码)
	 */
	@Expose
	private long chips;

	/**
	 * 用户状态
	 */
	@Expose(serialize = false, deserialize = false)
	private int state;
	/**
	 * 注册日期
	 */
	@Expose(serialize = false, deserialize = false)
	private String regdate;

	/**
	 * 用户所在房间
	 */
	@Expose(serialize = false, deserialize = false)
	private Room room;
	/**
	 * 是否已经弃牌
	 */
	@Expose
	private boolean isFold = true;
	/**
	 * 手牌
	 */
	private int[] handPokers;
	/**
	 * 是否已经看牌（拼三张）
	 */
	@Expose
	private boolean isLook = false;

	public boolean isLook() {
		return isLook;
	}

	public void setLook(boolean isLook) {
		this.isLook = isLook;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUserpwd() {
		return userpwd;
	}

	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public int getPic() {
		return pic;
	}

	public void setPic(int pic) {
		this.pic = pic;
	}

	public long getChips() {
		return chips;
	}

	public void setChips(long chips) {
		this.chips = chips;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getRegdate() {
		return regdate;
	}

	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public int[] getHandPokers() {
		return handPokers;
	}

	public void setHandPokers(int[] handPokers) {
		this.handPokers = handPokers;
	}

	public boolean isFold() {
		return isFold;
	}

	public void setFold(boolean fold) {
		this.isFold = fold;
	}

}
