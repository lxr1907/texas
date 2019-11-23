package yuelj.texas;

import java.util.ArrayList;
import java.util.List;

/**
 * 接口对应关系类
 * 
 * @author ausu
 *
 */
public class CtrlList {
	public static List<String[]> clist;

	static {
		clist = new ArrayList<String[]>();
		clist.add(new String[] { "playerService", "register" });// 0注册
		clist.add(new String[] { "playerService", "login" });// 1登录
		clist.add(new String[] { "roomService", "inRoom" });// 2进入房间
		clist.add(new String[] { "roomService", "outRoom" });// 3退出房间
		clist.add(new String[] { "playerService", "sitDown" });// 4坐下
		clist.add(new String[] { "playerService", "standUp" });// 5站起
		clist.add(new String[] { "playerService", "check" });// 6过牌
		clist.add(new String[] { "playerService", "betChips" });// 7下注
		clist.add(new String[] { "playerService", "fold" });// 8弃牌
		clist.add(new String[] { "lobbyService", "getRankList" });// 9获取排行榜
		clist.add(new String[] { "roomService", "lookCards" });// 10查看自己的手牌（拼三张）
		clist.add(new String[] { "roomService", "compareCards" });// 11和下家比牌（拼三张）
		clist.add(new String[] { "roomService", "sendMessage" });// 12发送表情或消息
	}

}
