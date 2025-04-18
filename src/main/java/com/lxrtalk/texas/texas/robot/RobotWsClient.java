package com.lxrtalk.texas.texas.robot;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Random;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxrtalk.texas.entity.PrivateRoom;
import com.lxrtalk.texas.entity.RetMsg;
import com.lxrtalk.texas.utils.serialize.JsonUtils;

@ClientEndpoint
public class RobotWsClient {
	private static Logger logger = LogManager.getLogger(RobotWsClient.class);
	// 缓冲区最大大小
	static final int maxSize = 4 * 1024;// ;// 1K
	static final String urlServer = "ws://127.0.0.1:8080/texas/ws/texas";
	private static URI uri;
	static {
		try {
			uri = new URI(urlServer);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			logger.error("",e);
		}
	}
	public Session session;
	public RobotPlayer player;
	public PrivateRoom roomInfo;
	//设定机器人的退出时间为1到15分钟随机
	Random random = new Random();
	public Date logOutTime = new Date(new Date().getTime() + random.nextInt(15) * 60 * 1000l);

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
			logger.error("",e);
		}
	}

	public void sendText(String message) {
		// 发送文本消息
		try {
			session.getBasicRemote().sendText(message);
		} catch (java.lang.IllegalStateException e) {
			// TODO Auto-generated catch block
			logger.error("",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("",e);
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
			if (retMsg.getC().equals("onGameEnd")) {
				RobotOperationsUtil.onGameEnd(this, retMsg);
			}
		} catch (Exception e) {
			logger.error("",e);
		}
	}

	@OnError
	public void onError(Throwable e) {
		logger.error("",e);
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

	public Date getLogOutTime() {
		return logOutTime;
	}

	public void setLogOutTime(Date logOutTime) {
		this.logOutTime = logOutTime;
	}
}
