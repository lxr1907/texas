package com.lxrtalk.texas.action.websocket;

import com.lxrtalk.texas.entity.BaseEntity;
import com.lxrtalk.texas.entity.Player;
import com.lxrtalk.texas.entity.SystemLogEntity;
import com.lxrtalk.texas.service.LobbyService;
import com.lxrtalk.texas.service.PlayerService;
import com.lxrtalk.texas.service.RoomService;
import com.lxrtalk.texas.service.SystemLogService;
import com.lxrtalk.texas.texas.BeanUtil;
import com.lxrtalk.texas.texas.CtrlList;
import com.lxrtalk.texas.texas.TexasStatic;
import com.lxrtalk.texas.utils.SpringUtil;
import com.lxrtalk.texas.utils.dateTime.DateUtil;
import com.lxrtalk.texas.utils.serialize.JsonUtils;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 德州入口
 *
 * @author lxr
 */
@ServerEndpoint(value = "/ws/texas", configurator = SpringAwareEndpointConfigurator.class)
@Component
public class TexasWS {
    // 缓冲区最大大小
    static final int maxSize = 1024;// 1 * 1024;// 1K

    private static Logger logger = LogManager.getLogger(TexasWS.class);

    @Autowired
    private PlayerService playerService;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private SystemLogService systemLogService;

    @OnMessage
    public void onMessage(String message, Session session) throws IOException, InterruptedException {
        logger.info("onMessage:" + message);
        onMessageDo(message, session);
    }

    public void onMessageDo(String message, Session session) {
        BaseEntity be = JsonUtils.fromJson(message, BaseEntity.class);
        int c = be.getC();
        try {
            switch (c) {
                case 0:// 0注册
                    playerService.register(session, message);
                    break;
                case 1:// 1登录
                    playerService.login(session, message);
                    break;
                case 2:// 2进入房间
                    roomService.inRoom(session, message);
                    break;
                case 3:// 3退出房间
                    roomService.outRoom(session, message);
                    break;
                case 4:// 4坐下
                    playerService.sitDown(session, message);
                    break;
                case 5:// 5站起
                    playerService.standUp(session, message);
                    break;
                case 6:// 6过牌
                    playerService.check(session, message);
                    break;
                case 7:// 7下注
                    playerService.betChips(session, message);
                    break;
                case 8:// 8弃牌
                    playerService.fold(session, message);
                    break;
                case 9:// 9获取排行榜
                    lobbyService.getRankList(session, message);
                    break;
                case 10:// 10查看自己的手牌（拼三张）
                    roomService.lookCards(session, message);
                    break;
                case 11:// 11和下家比牌（拼三张）
                    roomService.compareCards(session, message);
                    break;
                case 12:// 12发送表情或消息
                    roomService.sendMessage(session, message);
                    break;
            }

        } catch (Exception e) {
            String retMsg = "{\"c\":\"onException\",\"state\":0,\"message\":\"系统异常" + e.getMessage() + "\"}";
            sendText(session, retMsg);
            logger.error("onMessageDo", e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        logger.info("onOpen");
        // 可以缓冲的传入二进制消息的最大长度
        session.setMaxBinaryMessageBufferSize(maxSize);
        // 可以缓冲的传入文本消息的最大长度
        session.setMaxTextMessageBufferSize(maxSize);

    }

    @OnClose
    public void onClose(Session session) {
        onConnectLost(session);
        logger.info(" connection closed ");
    }

    @OnError
    public void onError(Session session, Throwable e) {
        onConnectLost(session);
        logger.info(" connection error: " + e.getMessage());
        logger.error("",e);
    }

    public void onConnectLost(Session session) {
        Player p = TexasStatic.loginPlayerMap.get(session.getId());
        // 从登录玩家列表中移除玩家信息
        if (p != null && p.getRoom() != null) {
            RoomService roomService = (RoomService) SpringUtil.getBean("roomService");
            roomService.outRoom(session, "", false);
        }
        TexasStatic.loginPlayerMap.remove(session.getId());
        if (p != null) {
            TexasStatic.playerSessionMap.remove(p.getId());
        }
    }

    /**
     * 发送文本消息
     */
    public static void sendText(Session session, String text) {
        if (session == null) {
            return;
        }
        synchronized (session) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(text);
                } catch (IOException e) {
                    logger.error("",e);
                }
            }
        }
    }

}
