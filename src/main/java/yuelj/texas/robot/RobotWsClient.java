package yuelj.texas.robot;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import yuelj.entity.PrivateRoom;
import yuelj.entity.RetMsg;
import yuelj.utils.serialize.JsonUtils;

@ClientEndpoint
public class RobotWsClient {
	private Logger logger = LogManager.getLogger(RobotWsClient.class);
	// 缓冲区最大大小
	static final int maxSize = 4 * 1024;// ;// 1K
	static final String urlServer = "ws://127.0.0.1:8080/texas/ws/texas";
	private static URI uri;
	static {
		try {
			uri = new URI(urlServer);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Session session;
	public RobotPlayer player;
	public PrivateRoom roomInfo;

	public boolean loginOnConnect;

	public RobotWsClient(boolean loginOnConnect) {
		this.loginOnConnect = loginOnConnect;
		try {
			// 获取WebSocket连接器，
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			// 连接会话
			session = container.connectToServer(this, uri);// 可以缓冲的传入二进制消息的最大长度
			session.setMaxBinaryMessageBufferSize(maxSize);
			// 可以缓冲的传入文本消息的最大长度
			session.setMaxTextMessageBufferSize(maxSize);
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
		logger.info("Robot Connected to endpoint: " + session.getBasicRemote());
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

	public PrivateRoom getRoomInfo() {
		return roomInfo;
	}

	public void setRoomInfo(PrivateRoom roomInfo) {
		this.roomInfo = roomInfo;
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

	public boolean isLoginOnConnect() {
		return loginOnConnect;
	}

	public void setLoginOnConnect(boolean loginOnConnect) {
		this.loginOnConnect = loginOnConnect;
	}
}
