package com.lxrtalk.texas.service.impl;

import jakarta.websocket.Session;

import org.springframework.stereotype.Service;

import com.lxrtalk.texas.service.RoomService;
import com.lxrtalk.texas.texas.TexasUtil;
import com.lxrtalk.texas.texas.threeCard.ThreeCardsUtil;

@Service("roomService")
public class RoomServiceImpl implements RoomService {
	
	@Override
	public void inRoom(Session session, String message) {
		TexasUtil.inRoom(session, message);
	}


	@Override
	public void outRoom(Session session, String message, boolean sendOrNot) {
		TexasUtil.outRoom(session, message, sendOrNot);
	}
	@Override
	public void outRoom(Session session, String message) {
		TexasUtil.outRoom(session, message, true);
	}

	public int getRoomLevel(String message) {
		return TexasUtil.getRoomMessage(message).getLevel();
	}

	@Override
	public void lookCards(Session session, String message) {
		ThreeCardsUtil.lookCards(session, message);
	}

	@Override
	public void compareCards(Session session, String message) {
		ThreeCardsUtil.compareCards(session, message);
	}


	@Override
	public void sendMessage(Session session, String message) {
		TexasUtil.sendMessage(session, message);
	}

}
