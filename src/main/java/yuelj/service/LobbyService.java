package yuelj.service;

import javax.websocket.Session;

import org.springframework.stereotype.Service;

@Service("lobbyService")
public interface LobbyService {
	public void getRankList(Session session, String message);
}
