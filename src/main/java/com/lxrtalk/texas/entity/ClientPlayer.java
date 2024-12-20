package com.lxrtalk.texas.entity;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
/**
 * 玩家实体
 * @author Ming
 *
 */
public class ClientPlayer extends BaseEntity implements Serializable {

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
	
}
