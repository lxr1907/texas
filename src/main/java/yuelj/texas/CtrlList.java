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
	private static List<String> clist;

	public static List<String> getClist() {
		if (clist == null) {
			clist = new ArrayList<String>();
			clist.add("playerService.register");// 0注册
			clist.add("playerService.login");// 1登录
			clist.add("roomService.inRoom");// 2进入房间
			clist.add("roomService.outRoom");// 3退出房间
			clist.add("playerService.sitDown");// 4坐下
			clist.add("playerService.standUp");// 5站起
			clist.add("playerService.check");// 6过牌
			clist.add("playerService.betChips");// 7下注
			clist.add("playerService.fold");// 8弃牌
			clist.add("lobbyService.getRankList");// 9获取排行榜
			clist.add("roomService.lookCards");// 10查看自己的手牌（拼三张）
			clist.add("roomService.compareCards");// 11和下家比牌（拼三张）
			clist.add("roomService.sendMessage");// 12发送表情或消息
		}
		return clist;
	}

	public CtrlList() {

	}

}
