package yuelj.action.websocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import yuelj.entity.BaseEntity;
import yuelj.entity.Player;
import yuelj.entity.SystemLogEntity;
import yuelj.service.LobbyService;
import yuelj.service.PlayerService;
import yuelj.service.RoomService;
import yuelj.service.SystemLogService;
import yuelj.texas.BeanUtil;
import yuelj.texas.CtrlList;
import yuelj.texas.TexasStatic;
import yuelj.utils.SpringUtil;
import yuelj.utils.serialize.JsonUtils;

/**
 * 德州入口
 * 
 * @author lxr
 *
 */
@ServerEndpoint("/ws/texas")
@Component
public class TexasWS {
	// 缓冲区最大大小
	static final int maxSize = 256;// 1 * 1024;// 1K

	private Logger logger = LogManager.getLogger(TexasWS.class);

	@OnMessage
	public void onMessage(String message, Session session) throws IOException, InterruptedException {
		logger.info("onMessage:" + message);
		// onMessageDoReflect(message, session);
		onMessageDo(message, session);
	}

	public void onMessageDo(String message, Session session) {
		BaseEntity be = JsonUtils.fromJson(message, BaseEntity.class);
		int c = be.getC();
		try {
			switch (c) {
			case 0:// 0注册
				((PlayerService) SpringUtil.getBean("playerService")).register(session, message);
				break;
			case 1:// 1登录
				((PlayerService) SpringUtil.getBean("playerService")).login(session, message);
				break;
			case 2:// 2进入房间
				((RoomService) SpringUtil.getBean("roomService")).inRoom(session, message);
				break;
			case 3:// 3退出房间
				((RoomService) SpringUtil.getBean("roomService")).outRoom(session, message);
				break;
			case 4:// 4坐下
				((PlayerService) SpringUtil.getBean("playerService")).sitDown(session, message);
				break;
			case 5:// 5站起
				((PlayerService) SpringUtil.getBean("playerService")).standUp(session, message);
				break;
			case 6:// 6过牌
				((PlayerService) SpringUtil.getBean("playerService")).check(session, message);
				break;
			case 7:// 7下注
				((PlayerService) SpringUtil.getBean("playerService")).betChips(session, message);
				break;
			case 8:// 8弃牌
				((PlayerService) SpringUtil.getBean("playerService")).fold(session, message);
				break;
			case 9:// 9获取排行榜
				((LobbyService) SpringUtil.getBean("lobbyService")).getRankList(session, message);
				break;
			case 10:// 10查看自己的手牌（拼三张）
				((RoomService) SpringUtil.getBean("roomService")).lookCards(session, message);
				break;
			case 11:// 11和下家比牌（拼三张）
				((RoomService) SpringUtil.getBean("roomService")).compareCards(session, message);
				break;
			case 12:// 12发送表情或消息
				((RoomService) SpringUtil.getBean("roomService")).sendMessage(session, message);
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			SystemLogService syslogService = (SystemLogService) SpringUtil.getBean("SystemLogServiceImpl");
			SystemLogEntity entity = new SystemLogEntity();
			entity.setType(c + "");
			entity.setOperation(message);
			StackTraceElement[] eArray = e.getCause().getStackTrace();
			String errorMessage = "";
			for (int i = 0; i < eArray.length; i++) {
				String className = e.getCause().getStackTrace()[i].getClassName();
				String MethodName = e.getCause().getStackTrace()[i].getMethodName();
				int LineNumber = e.getCause().getStackTrace()[i].getLineNumber();
				errorMessage = errorMessage + "\n---" + className + "." + MethodName + ",line:" + LineNumber;
			}
			entity.setContent(e.getCause() + errorMessage);
			entity.setDatetime(yuelj.utils.dateTime.DateUtil.nowDatetime());
			syslogService.insertSystemLog(entity);
			String retMsg = "{\"c\":\"onException\",\"state\":0,\"message\":\"系统异常" + errorMessage + "\"}";
			sendText(session, retMsg);
			logger.info(e.getCause() + errorMessage);
		}
	}

	public void onMessageDoReflect(String message, Session session) {
		String ctrl[] = getCtrl(message);
		try {
			BeanUtil.invokeMethod(SpringUtil.getBean(ctrl[0]), ctrl[1], session, message);
		} catch (Exception e) {
			e.printStackTrace();
			SystemLogService syslogService = (SystemLogService) SpringUtil.getBean("SystemLogServiceImpl");
			SystemLogEntity entity = new SystemLogEntity();
			entity.setType(ctrl[1]);
			entity.setOperation(message);
			StackTraceElement[] eArray = e.getCause().getStackTrace();
			String errorMessage = "";
			for (int i = 0; i < eArray.length; i++) {
				String className = e.getCause().getStackTrace()[i].getClassName();
				String MethodName = e.getCause().getStackTrace()[i].getMethodName();
				int LineNumber = e.getCause().getStackTrace()[i].getLineNumber();
				errorMessage = errorMessage + "\n---" + className + "." + MethodName + ",line:" + LineNumber;
			}
			entity.setContent(e.getCause() + errorMessage);
			entity.setDatetime(yuelj.utils.dateTime.DateUtil.nowDatetime());
			syslogService.insertSystemLog(entity);
			String retMsg = "{\"c\":\"onException\",\"state\":0,\"message\":\"系统异常" + errorMessage + "\"}";
			sendText(session, retMsg);
			logger.info(e.getCause() + errorMessage);
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
		e.printStackTrace();
	}

	public void onConnectLost(Session session) {
		Player p = TexasStatic.loginPlayerMap.get(session.getId());
		// 从登录玩家列表中移除玩家信息
		if (p != null && p.getRoom() != null) {
			RoomService roomService = (RoomService) SpringUtil.getBean("roomService");
			roomService.outRoom(session, "", false);
		}
		TexasStatic.loginPlayerMap.remove(session.getId());
		TexasStatic.playerSessionMap.remove(p.getId());
	}

	/**
	 * 发送文本消息
	 * 
	 * @param session
	 * @param text
	 */
	public static void sendText(Session session, String text) {
		if (session == null) {
			return;
		}
		synchronized (session) {
			if (session.isOpen()) {
				try {
					session.getBasicRemote().sendText(text);
					// logger.info(text);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * [类的别名 ,方法名]
	 * 
	 * @param message
	 * @return
	 */
	private String[] getCtrl(String message) {
		BaseEntity be = JsonUtils.fromJson(message, BaseEntity.class);
		int c = be.getC();
		return CtrlList.clist.get(c);
	}

}
