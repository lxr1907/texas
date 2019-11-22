package yuelj.entity;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

import yuelj.texas.Room;

/**
 * 私有房间信息（包含自己的手牌）
 * @author Ming
 *
 */
public class PrivateRoom implements Serializable {

	private static final long serialVersionUID = 1L;
	@Expose
	private Room room;
	@Expose
	private int[] handPokers;

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

}
