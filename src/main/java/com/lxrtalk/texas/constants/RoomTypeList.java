package com.lxrtalk.texas.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.lxrtalk.texas.texas.Room;
import com.lxrtalk.texas.texas.threeCard.ThreeCardRoom;

/**
 * 房间类型列表
 * 
 * @author Ming
 *
 */
public class RoomTypeList {
	public static Map<Integer, Room> roomTypeMap = new HashMap<Integer, Room>();
	public static Map<Integer, ThreeCardRoom> threeCardRoomTypeMap = new HashMap<Integer, ThreeCardRoom>();
	static {
		roomTypeMap.put(0, getNewRoom(0));
		roomTypeMap.put(1, getNewRoom(1));
		roomTypeMap.put(2, getNewRoom(2));
		threeCardRoomTypeMap.put(0, getThreeCardRoom(0));
		threeCardRoomTypeMap.put(1, getThreeCardRoom(1));
		threeCardRoomTypeMap.put(2, getThreeCardRoom(2));
	}

	/**
	 * 根据级别获取德州扑克房间
	 */
	public static Room getNewRoom(int level) {
		Room room = new Room();
		int jMaxPlayer = 6;
		room.setMaxPlayers(6);
		room.setMinPlayers(2);
		room.setDealer(1);
		room.setRoomstate(1);
		room.setFreeSeatStack(getStack(jMaxPlayer));
		room.setOptTimeout(10 * 1000);
		room.setRestBetweenGame(8 * 1000);
		if (level == 0) {
			room.setLevel(0);
			room.setMaxChips(10000);
			room.setMinChips(100);
			room.setBigBet(100);
			room.setSmallBet(50);
		} else if (level == 1) {
			room.setLevel(1);
			room.setMaxChips(20000);
			room.setMinChips(20000);
			room.setBigBet(200);
			room.setSmallBet(100);
		} else if (level == 2) {
			room.setLevel(2);
			room.setMaxChips(50000);
			room.setMinChips(50000);
			room.setBigBet(500);
			room.setSmallBet(250);
		}
		return room;
	}

	/**
	 * 获取欢乐拼三张的房间
	 * 
	 * @param level
	 * @return
	 */
	public static ThreeCardRoom getThreeCardRoom(int level) {
		ThreeCardRoom room = new ThreeCardRoom();
		int jMaxPlayer = 6;
		room.setMaxPlayers(6);
		room.setMinPlayers(2);
		room.setDealer(1);
		room.setRoomstate(1);
		room.setFreeSeatStack(getStack(jMaxPlayer));
		room.setOptTimeout(10 * 1000);
		room.setRestBetweenGame(8 * 1000);
		//每人回收的台费
		room.setTableFeeBet(20);
		if (level == 0) {
			room.setLevel(0);
			//封顶带入
			room.setMaxChips(3000);
			//最小带入
			room.setMinChips(500);
			//三种看牌下注额度，小50，大100，双倍大200
			room.setDoubleBigBet(200);
			room.setBigBet(100);
			room.setSmallBet(50);
			//三种暗牌下注额度，小20，大40，双倍大80
			room.setSmallBlindBet(20);
			room.setBigBlindBet(40);
			room.setDoubleBigBlindBet(80);
		} else if (level == 1) {
			room.setLevel(1);
			room.setMaxChips(20000);
			room.setMinChips(20000);
			room.setBigBet(200);
			room.setSmallBet(100);
		} else if (level == 2) {
			room.setLevel(2);
			room.setMaxChips(50000);
			room.setMinChips(50000);
			room.setBigBet(500);
			room.setSmallBet(250);
		}
		return room;
	}

	public static Stack<Integer> getStack(int maxPlayer) {
		Stack<Integer> stack = new Stack<Integer>();
		for (int i = 0; i < maxPlayer; i++) {
			stack.push(i);
		}
		return stack;
	}

}
