package com.lxrtalk.texas.service;

import jakarta.websocket.Session;

public interface RoomService {

	public void inRoom(Session session, String message);

	public void outRoom(Session session, String message, boolean sendOrNot);

	public void outRoom(Session session, String message);

	public void lookCards(Session session, String message);

	public void compareCards(Session session, String message);

	public void sendMessage(Session session, String message);
}
