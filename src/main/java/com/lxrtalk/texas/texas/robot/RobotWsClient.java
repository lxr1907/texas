package com.lxrtalk.texas.texas.robot;

import com.lxrtalk.texas.entity.PrivateRoom;
import com.lxrtalk.texas.entity.RetMsg;
import com.lxrtalk.texas.utils.serialize.JsonUtils;
import jakarta.websocket.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Random;

@ClientEndpoint
public class RobotWsClient {
    private static final Logger logger = LogManager.getLogger(RobotWsClient.class);
    // 缓冲区最大大小
    static final int maxSize = 4 * 1024;
    static final String urlServer = "ws://127.0.0.1:8080/texas/ws/texas";
    private static URI uri;

    static {
        try {
            uri = new URI(urlServer);
        } catch (URISyntaxException e) {
            logger.error("", e);
        }
    }

    public Session session;
    public RobotPlayer player;
    public PrivateRoom roomInfo;
    //
    Random random = new Random();
    /**
     * 机器人退出时间
     * 设定机器人的退出时间为1到15分钟随机,超出机器人自动退出
     * 可以修改这个值来测试机器人的退出时间
     * 单位：分钟
     */
    public Date logOutTime = new Date(new Date().getTime() + random.nextInt(15) * 60 * 1000L);

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
            logger.error("", e);
        }
    }

    public void sendText(String message) {
        // 发送文本消息
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        logger.info("Robot Connected to endpoint: {}", session.getBasicRemote());
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
            logger.error("", e);
        }
    }

    @OnError
    public void onError(Throwable e) {
        RobotOperationsUtil.robotOut(this);
        logger.error("", e);
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
