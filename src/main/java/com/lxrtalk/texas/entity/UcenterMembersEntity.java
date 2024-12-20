package com.lxrtalk.texas.entity;

import com.google.gson.annotations.Expose;

public class UcenterMembersEntity {
	/**
	 * 账号
	 */
	@Expose
	private String username;
	@Expose
	private String uid;
	@Expose
	private String password;
	@Expose
	private String salt;
	@Expose
	private String email;
	@Expose
	private String myidkey;
	@Expose
	private String regdate;
	@Expose
	private String regip;
	@Expose
	private String lastloginip;
	@Expose
	private String lastlogintime;
	@Expose
	private String secques;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMyidkey() {
		return myidkey;
	}

	public void setMyidkey(String myidkey) {
		this.myidkey = myidkey;
	}

	public String getRegdate() {
		return regdate;
	}

	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}

	public String getRegip() {
		return regip;
	}

	public void setRegip(String regip) {
		this.regip = regip;
	}

	public String getLastloginip() {
		return lastloginip;
	}

	public void setLastloginip(String lastloginip) {
		this.lastloginip = lastloginip;
	}

	public String getLastlogintime() {
		return lastlogintime;
	}

	public void setLastlogintime(String lastlogintime) {
		this.lastlogintime = lastlogintime;
	}

	public String getSecques() {
		return secques;
	}

	public void setSecques(String secques) {
		this.secques = secques;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMyid() {
		return myid;
	}

	public void setMyid(String myid) {
		this.myid = myid;
	}

	@Expose
	private String myid;
}
