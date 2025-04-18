package com.lxrtalk.texas.entity;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class BaseEntity implements Serializable {
	private static final long serialVersionUID = -4908632621737577454L;
	@Expose
	public String id;

	/**
	 * 消息类型
	 */
	@Expose
	public int c;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}
	
}
