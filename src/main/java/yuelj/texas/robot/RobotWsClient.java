package yuelj.texas.robot;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import yuelj.entity.RetMsg;
import yuelj.texas.Room;
import yuelj.utils.logs.SystemLog;
import yuelj.utils.serialize.JsonUtils;

@ClientEndpoint
public class RobotWsClient {
	private static URI uri;
	public Session session;
	public RobotPlayer player;
	public Room roomInfo;

	public boolean loginOnConnect;

	public RobotWsClient(boolean loginOnConnect) {
		this.loginOnConnect = loginOnConnect;
		try {
			String urlServer = "ws://127.0.0.1:8080/texas/ws/texas";
			uri = new URI(urlServer);
			// texas,lxrtexas
			// 120.26.217.116，127.0.0.1
			// 获取WebSocket连接器，
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			// 连接会话
			session = container.connectToServer(this, uri);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendText(String message) {
		// 发送文本消息
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		SystemLog.printlog("Robot Connected to endpoint: " + session.getBasicRemote());
		if (loginOnConnect) {
			RobotOperationsUtil.robotLogin(this);
		}
	}

	@OnMessage
	public void onMessage(String message) {
		try {
			RetMsg retMsg = JsonUtils.fromJson(message, RetMsg.class);
			if (retMsg.getC().equals("onLogin")) {
				RobotOperationsUtil.onLogin(this, retMsg);
			}
			if (retMsg.getC().equals("onEnterRoom")) {
				RobotOperationsUtil.onEnterRoom(this, retMsg);
			}
			if (retMsg.getC().equals("onGameStart")) {
				RobotOperationsUtil.onGameStart(this, retMsg);
			}
			if (retMsg.getC().equals("onPlayerTurn")) {
				RobotOperationsUtil.onPlayerTurn(this, retMsg);
			}
			if (retMsg.getC().equals("onPlayerBet")) {
				RobotOperationsUtil.onPlayerBet(this, retMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnError
	public void onError(Throwable e) {
		e.printStackTrace();
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public RobotPlayer getPlayer() {
		return player;
	}

	public void setPlayer(RobotPlayer player) {
		this.player = player;
	}

	public Room getRoomInfo() {
		return roomInfo;
	}

	public void setRoomInfo(Room roomInfo) {
		this.roomInfo = roomInfo;
	}

	public boolean isLoginOnConnect() {
		return loginOnConnect;
	}

	public void setLoginOnConnect(boolean loginOnConnect) {
		this.loginOnConnect = loginOnConnect;
	}
}
