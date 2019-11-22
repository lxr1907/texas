package yuelj.action.websocket;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import yuelj.entity.BaseEntity;
import yuelj.entity.Player;
import yuelj.entity.SystemLogEntity;
import yuelj.service.RoomService;
import yuelj.service.SystemLogService;
import yuelj.texas.BeanUtil;
import yuelj.texas.CtrlList;
import yuelj.texas.TexasStatic;
import yuelj.utils.SpringUtil;
import yuelj.utils.logs.SystemLog;
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

	@OnMessage
	public void onMessage(String message, Session session) throws IOException, InterruptedException {
		SystemLog.printlog("onMessage:" + message);
		String ctrl[] = getCtrl(message);
		try {
			Date now=new Date();
			BeanUtil.invokeMethod(SpringUtil.getBean(ctrl[0]), ctrl[1], session, message);
			Date costEnd=new Date();
			long cost=costEnd.getTime()-now.getTime();
			if(cost>500){
				SystemLog.printPerformance("onMessage:" + message+" cost Millisecond"+cost );
			}
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
			SystemLog.printlog(e.getCause() + errorMessage);
		}
	}

	@OnOpen
	public void onOpen(Session session) {
		SystemLog.printlog("onOpen");
	}

	@OnClose
	public void onClose(Session session) {
		onConnectLost(session);
		SystemLog.printlog(" connection closed ");
	}

	@OnError
	public void onError(Session session, Throwable e) {
		onConnectLost(session);
		SystemLog.printlog(" connection error: " + e.getMessage());
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
					// SystemLog.printlog(text);
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
		List<String> clist = CtrlList.getClist();
		String s = clist.get(c);
		String[] ctrl = s.split("\\.");
		return ctrl;
	}

	public static void main(String[] args) {
		String s = "a.b";
		String ss[] = s.split("\\.");
		SystemLog.printlog(ss.length);
		for (int i = 0; i < ss.length; i++) {
			SystemLog.printlog(ss[i]);
		}
	}
}
