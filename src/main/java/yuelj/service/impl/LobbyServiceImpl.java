package yuelj.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.websocket.Session;

import org.springframework.stereotype.Service;

import yuelj.entity.Player;
import yuelj.entity.RetMsg;
import yuelj.service.LobbyService;
import yuelj.texas.TexasUtil;
import yuelj.utils.serialize.JsonUtils;

@Service("lobbyService")
public class LobbyServiceImpl implements LobbyService {
	/**
	 * 玩家根据筹码数量的排行榜
	 */
	public static List<Player> rankList = new CopyOnWriteArrayList<Player>();
	/**
	 * 排行榜中最后一名的筹码数
	 */
	public static Long minChips = 0l;
	/**
	 * 排行榜的大小
	 */
	public static int rankSize = 50;;

	@Override
	public void getRankList(Session session, String message) {
		int size = 10;
		int realSize = rankList.size();
		if (realSize < size) {
			size = realSize;
		}
		String msg = JsonUtils.toJson(rankList.subList(0, size), rankList.getClass());
		RetMsg rm = new RetMsg();
		rm.setC("onGetRankList");
		rm.setState(1);
		rm.setMessage(msg);
		String retMsg = JsonUtils.toJson(rm, RetMsg.class);
		TexasUtil.sendMsgToOne(session, retMsg);
	}

	/**
	 * 更新排行榜
	 * 
	 * @param p
	 */
	public static void updateRankList(Player p) {
		// 机器人不加入排行榜
		if (p.getUsername().contains("robot")) {
			return;
		}
		long chips = p.getChips() + p.getBodyChips();
		// 新增排行榜玩家信息
		Player rankPlayer = new Player();
		rankPlayer.setId(p.getId());
		rankPlayer.setChips(chips);
		rankPlayer.setUsername(p.getUsername());
		if (chips < minChips && rankList.size() >= rankSize) {
			return;
		}
		synchronized (rankList) {
			boolean hasInRank = false;
			for (Player player : rankList) {
				if (player.getId().equals(rankPlayer.getId())) {
					player.setChips(chips);
					hasInRank = true;
				}
			}
			if (!hasInRank) {
				rankList.add(rankPlayer);
			}
			Collections.sort(rankList, new Comparator<Player>() {
				public int compare(Player entry1, Player entry2) {
					Long value1 = 0l, value2 = 0l;
					value1 = entry1.getChips();
					value2 = entry2.getChips();
					return value2.compareTo(value1);
				}
			});
			if (rankList.size() > rankSize) {
				rankList = rankList.subList(0, rankSize);
			}
			minChips = rankList.get(rankList.size() - 1).getChips();
		}
	}

}
