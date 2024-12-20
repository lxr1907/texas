package com.lxrtalk.texas.service;

import jakarta.websocket.Session;

public interface LobbyService {
	public void getRankList(Session session, String message);
}
