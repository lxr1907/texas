package yuelj.service;

import javax.websocket.Session;

public interface LobbyService {
	public void getRankList(Session session, String message);
}
