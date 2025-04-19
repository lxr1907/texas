package com.lxrtalk.texas.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.lxrtalk.texas.texas.TexasRoom;
import com.lxrtalk.texas.texas.TexasRoomConfig;
import com.lxrtalk.texas.texas.threeCard.ThreeCardTexasRoom;
import com.lxrtalk.texas.utils.SpringUtil;

/**
 * 房间类型列表
 *
 * @author Ming
 */
public class RoomTypeList {
    public static Map<Integer, TexasRoomConfig> roomTypeMap = new HashMap<Integer, TexasRoomConfig>();
    public static Map<Integer, ThreeCardTexasRoom> threeCardRoomTypeMap = new HashMap<Integer, ThreeCardTexasRoom>();

    static {
        roomTypeMap.put(0, getRoomConfig(0));
        roomTypeMap.put(1, getRoomConfig(1));
        roomTypeMap.put(2, getRoomConfig(2));
        threeCardRoomTypeMap.put(0, getThreeCardRoom(0));
        threeCardRoomTypeMap.put(1, getThreeCardRoom(1));
        threeCardRoomTypeMap.put(2, getThreeCardRoom(2));
    }

    /**
     * 获取德州扑克房间配置
     */
    public static TexasRoomConfig getRoomConfig(int level) {
        TexasRoomConfig texasRoom = new TexasRoomConfig();
        texasRoom.setMaxPlayers(6);
        texasRoom.setMinPlayers(2);
        texasRoom.setOptTimeout(10 * 1000);
        texasRoom.setRestBetweenGame(8 * 1000);
        texasRoom.setLevel(level);
        if (level == 0) {
            texasRoom.setMaxChips(10000);
            texasRoom.setMinChips(100);
            texasRoom.setBigBet(100);
            texasRoom.setSmallBet(50);
        } else if (level == 1) {
            texasRoom.setMaxChips(20000);
            texasRoom.setMinChips(20000);
            texasRoom.setBigBet(200);
            texasRoom.setSmallBet(100);
        } else if (level == 2) {
            texasRoom.setMaxChips(50000);
            texasRoom.setMinChips(50000);
            texasRoom.setBigBet(500);
            texasRoom.setSmallBet(250);
        }
        return texasRoom;
    }

    /**
     * 根据级别获取德州扑克房间
     */
    public static TexasRoom getNewRoom(int level) {
        TexasRoom texasRoom = (TexasRoom) SpringUtil.getBean("texasRoom");
        var config = roomTypeMap.get(level);
        texasRoom.setConfig(config);
        texasRoom.setDealer(1);
        texasRoom.setRoomstate(1);
        texasRoom.setFreeSeatStack(getStack(config.getMaxPlayers()));
        texasRoom.setLevel(level);
        return texasRoom;
    }

    /**
     * 获取欢乐拼三张的房间
     *
     * @param level
     * @return
     */
    public static ThreeCardTexasRoom getThreeCardRoom(int level) {
        ThreeCardTexasRoom room = new ThreeCardTexasRoom();
        int jMaxPlayer = 6;
        room.setConfig(roomTypeMap.get(level));
        room.getConfig().setMaxPlayers(6);
        room.getConfig().setMinPlayers(2);
        room.setDealer(1);
        room.setRoomstate(1);
        room.setFreeSeatStack(getStack(jMaxPlayer));
        room.getConfig().setOptTimeout(10 * 1000);
        room.getConfig().setRestBetweenGame(8 * 1000);
        //每人回收的台费
        room.setTableFeeBet(20);
        if (level == 0) {
            room.setLevel(0);
            //封顶带入
            room.getConfig().setMaxChips(3000);
            //最小带入
            room.getConfig().setMinChips(500);
            //三种看牌下注额度，小50，大100，双倍大200
            room.setDoubleBigBet(200);
            room.getConfig().setBigBet(100);
            room.getConfig().setSmallBet(50);
            //三种暗牌下注额度，小20，大40，双倍大80
            room.setSmallBlindBet(20);
            room.setBigBlindBet(40);
            room.setDoubleBigBlindBet(80);
        } else if (level == 1) {
            room.setLevel(1);
            room.getConfig().setMaxChips(20000);
            room.getConfig().setMinChips(20000);
            room.getConfig().setBigBet(200);
            room.getConfig().setSmallBet(100);
        } else if (level == 2) {
            room.setLevel(2);
            room.getConfig().setMaxChips(50000);
            room.getConfig().setMinChips(50000);
            room.getConfig().setBigBet(500);
            room.getConfig().setSmallBet(250);
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
