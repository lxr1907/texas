package yuelj.texas;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import yuelj.entity.Player;
import yuelj.texas.threeCard.ThreeCardRoom;

public class TexasStatic {
	/**
	 * 登录玩家列表
	 */
	public static ConcurrentMap<String,Player> loginPlayerMap = new ConcurrentHashMap<String, Player>();
	/**
	 * 登录玩家列表
	 */
	public static ConcurrentMap<String,String> playerSessionMap = new ConcurrentHashMap<String, String>();

	/**
	 * 房间列表，德州扑克
	 */
	public static List<Room> roomList=new CopyOnWriteArrayList<Room>();
	/**
	 * 房间列表，拼三张
	 */
	public static List<ThreeCardRoom> threeCardRoomList=new CopyOnWriteArrayList<ThreeCardRoom>();

}
