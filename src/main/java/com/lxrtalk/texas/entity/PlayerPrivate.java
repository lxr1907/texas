package com.lxrtalk.texas.entity;

import com.google.gson.annotations.Expose;

public class PlayerPrivate extends Player {
	private static final long serialVersionUID = -7275579813476459537L;

	/**
	 * 手牌
	 */
	@Expose
	private int[] handPokers;

	public int[] getHandPokers() {
		return handPokers;
	}

	public void setHandPokers(int[] handPokers) {
		this.handPokers = handPokers;
	}
}
