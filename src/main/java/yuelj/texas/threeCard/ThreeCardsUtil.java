package yuelj.texas.threeCard;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.websocket.Session;

import yuelj.cardUtils.ThreeCardCompareUtil;
import yuelj.entity.Player;
import yuelj.entity.PrivateRoom;
import yuelj.entity.RetMsg;
import yuelj.texas.Room;
import yuelj.texas.TexasUtil;
import yuelj.utils.serialize.JsonUtils;

/**
 * 欢乐拼三张工具类
 * 
 * @author ausu
 *
 */
public class ThreeCardsUtil {
	/**
	 * 看自己的手牌
	 * 
	 * @param session
	 * @param message
	 */
	public static void lookCards(Session session, String message) {
		Player currPlayer = TexasUtil.getPlayerBySessionId(session.getId());
		// 判断是否轮到当前玩家操作
		if (currPlayer == null || currPlayer.getRoom() == null
				|| currPlayer.getSeatNum() != currPlayer.getRoom().getNextturn()) {
			return;
		}
		if (currPlayer.getRoom().getGamestate().get() != 1) {
			return;
		}
		synchronized (currPlayer) {
			// 设置已看牌
			currPlayer.setLook(true);
			// 私有房间信息（手牌）
			PrivateRoom pRoom = new PrivateRoom();
			pRoom.setRoom(currPlayer.getRoom());
			pRoom.setHandPokers(currPlayer.getHandPokers());
			String msg = JsonUtils.toJson(pRoom, PrivateRoom.class);
			RetMsg rm = new RetMsg();
			rm.setC("onLookCards");
			rm.setState(1);
			rm.setMessage(msg);
			// 发送玩家私有信息，手牌
			String retMsg = JsonUtils.toJson(rm, RetMsg.class);
			TexasUtil.sendMsgToOne(currPlayer, retMsg);
			// 通知所有房间内玩家，有玩家看牌
			TexasUtil.sendPlayerToOthers(currPlayer, currPlayer.getRoom(), "onPlayerLookCards");
		}
	}

	/**
	 * 比牌，和下家比牌 如果只剩余2个玩家，则比牌并结束游戏
	 * 
	 * @param session
	 * @param message
	 */
	public static void compareCards(Session session, String message) {
		Player currPlayer = TexasUtil.getPlayerBySessionId(session.getId());
		// 判断是否轮到自己
		if (currPlayer == null || currPlayer.getRoom() == null
				|| currPlayer.getSeatNum() != currPlayer.getRoom().getNextturn()) {
			return;
		}
		if (currPlayer.getRoom().getGamestate().get() != 1) {
			return;
		}
		ThreeCardRoom room = (ThreeCardRoom) currPlayer.getRoom();
		int betBase = 0;
		// 看牌下注基数和暗牌下注基数不同
		if (currPlayer.isLook()) {
			betBase = room.getSmallBet();
		} else {
			betBase = room.getSmallBlindBet();
		}
		// 比牌所需筹码
		int chip = room.getRoundMaxBet() * betBase;
		// 帮玩家下比牌所需筹码
		boolean success = room.betCompareCard(currPlayer, chip);
		if (!success) {
			return;
		}
		int nextPlayerSeatNum = TexasUtil.getNextSeatNum(currPlayer.getSeatNum(), currPlayer.getRoom(), false);
		Player nextPlayer = TexasUtil.getPlayerBySeatNum(nextPlayerSeatNum, currPlayer.getRoom().getIngamePlayers());
		// 判断和下家哪个人牌大
		List<Integer> listnew = Arrays.stream(currPlayer.getHandPokers()).boxed().collect(Collectors.toList());
		List<Integer> listold = Arrays.stream(nextPlayer.getHandPokers()).boxed().collect(Collectors.toList());
		int result = ThreeCardCompareUtil.compareValue(listnew, listold);
		Player losePlayer = currPlayer;
		if (result == 1) {
			losePlayer = nextPlayer;
		}
		// 牌小的自动弃牌出局
		room.loseCompareCards(losePlayer);
	}

	/**
	 * 为房间中正在游戏的玩家分配手牌
	 * 
	 * @param room
	 * @param player
	 */
	public static void assignHandPokerByRoom(Room room) {
		List<Integer> cardList = room.getCardList();
		for (Player p : room.getIngamePlayers()) {
			List<Integer> pCards=cardList.subList(0, 3);
			pCards=ThreeCardCompareUtil.getCardsGroupType(pCards);
			//三张牌和牌型
			int[] hankPoker = { pCards.get(0), pCards.get(1), pCards.get(2), pCards.get(3)};
			cardList.remove(0);
			cardList.remove(0);
			cardList.remove(0);
			// 玩家手牌
			p.setHandPokers(hankPoker);
		}
	}
}
