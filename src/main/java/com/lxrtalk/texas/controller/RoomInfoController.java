package com.lxrtalk.texas.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lxrtalk.texas.texas.TexasStatic;
import com.lxrtalk.texas.utils.serialize.JsonUtils;

@RestController
public class RoomInfoController {
	@RequestMapping("/roomList")
	public String roomList() {
		return JsonUtils.toListJson(TexasStatic.texasRoomList);
	}

	@RequestMapping("/roomCount")
	public String roomCount() {
		return TexasStatic.texasRoomList.size() + "";
	}

	@RequestMapping("/roomPlayers")
	public AtomicInteger roomPlayers() {
		AtomicInteger players = new AtomicInteger(0);
		TexasStatic.texasRoomList.parallelStream().forEach(room -> {
			players.getAndAdd(room.getIngamePlayers().size());
		});
		return players;
	}

	@RequestMapping("/statistics")
	public Map<String, String> statistics() {
		Map<String, String> map = new HashMap<String, String>();
		//总房间数量
		map.put("roomCount", TexasStatic.texasRoomList.size() + "");
		//游戏中玩家数量
		AtomicInteger ingamePlayers = new AtomicInteger(0);
		TexasStatic.texasRoomList.parallelStream().forEach(room -> {
			ingamePlayers.getAndAdd(room.getIngamePlayers().size());
		});
		map.put("ingamePlayers", ingamePlayers.toString());
		//等待开局玩家数量
		AtomicInteger waitPlayers = new AtomicInteger(0);
		TexasStatic.texasRoomList.parallelStream().forEach(room -> {
			waitPlayers.getAndAdd(room.getWaitPlayers().size());
		});
		map.put("waitPlayers", waitPlayers.toString());
		//总在线玩家数
		map.put("playersCount", TexasStatic.playerSessionMap.size()+"");
		map.put("loginPlayerMap", TexasStatic.loginPlayerMap.size()+"");
		
		return map;
	}
}
