package yuelj.service.impl;

import java.util.List;

import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yuelj.dao.PlayerDao;
import yuelj.entity.BetPlayer;
import yuelj.entity.PageEntity;
import yuelj.entity.Player;
import yuelj.entity.RetMsg;
import yuelj.service.PlayerService;
import yuelj.texas.TexasStatic;
import yuelj.texas.TexasUtil;
import yuelj.utils.SpringUtil;
import yuelj.utils.md5encrypt.Md5;
import yuelj.utils.serialize.JsonUtils;

@Service("playerService")
public class PlayerServiceImpl implements PlayerService {
	@Autowired
	private PlayerDao playerDao;

	@Override
	public List<Player> selectPlayers(Player player) {
		return playerDao.selectPlayers(player);
	}

	@Override
	public List<Player> selectLoginPlayer(Player player) {
		return playerDao.selectLoginPlayer(player);
	}

	@Override
	public String selectPlayersCount(Player player) {
		return playerDao.selectPlayersCount(player);
	}

	@Override
	public void insertPlayer(Player player) {
		playerDao.insertPlayer(player);
	}

	@Override
	public void updatePlayer(Player player) {
		playerDao.updatePlayer(player);
	}

	@Override
	public Player selectPlayer(Player player) {
		List<Player> list = playerDao.selectPlayers(player);
		if (list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	@Override
	public List<Player> selectPlayersPage(Player player, PageEntity page) {
		return playerDao.selectPlayersPage(player, page);
	}

	@Override
	public void login(Session session, String message) {
		Player player = new Player();
		player = JsonUtils.fromJson(message, Player.class);
		player.setUserpwd(Md5.GetMD5Code(player.getUserpwd()));

		PlayerService playerService = (PlayerService) SpringUtil.getBean("playerService");
		Player currPlayer = playerService.selectPlayer(player);
		RetMsg rm = new RetMsg();
		rm.setC("onLogin");
		if (currPlayer == null) {
			rm.setState(0);
			rm.setMessage("????????????");
		} else {
			rm.setState(1);
			rm.setMessage(JsonUtils.toJson(currPlayer, Player.class));
			// ????????????sessionId??????????????????????????????
			currPlayer.setSession(session);
			TexasStatic.loginPlayerMap.put(session.getId(), currPlayer);
			//
			String oldsessionId = TexasStatic.playerSessionMap.get(currPlayer.getId());
			if (oldsessionId != null) {
				Player oldPlayer = TexasStatic.loginPlayerMap.get(oldsessionId);
				TexasUtil.outRoom(oldPlayer);
				TexasStatic.loginPlayerMap.remove(oldsessionId);
				try {
					if (oldPlayer != null && oldPlayer.getSession() != null) {
						oldPlayer.getSession().close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			TexasStatic.playerSessionMap.put(currPlayer.getId(), session.getId());

		}
		String retMsg = JsonUtils.toJson(rm, RetMsg.class);
		TexasUtil.sendMsgToOne(session, retMsg);
	}

	@Override
	public void register(Session session, String message) {
		PlayerService playerService = (PlayerService) SpringUtil.getBean("playerService");
		RetMsg rm = new RetMsg();
		rm.setC("onRegister");
		Player player = new Player();
		player = JsonUtils.fromJson(message, Player.class);
		player.setUserpwd(Md5.GetMD5Code(player.getUserpwd()));
		Player playerTemp = new Player();
		playerTemp.setUsername(player.getUsername());
		if (playerService.selectPlayer(playerTemp) == null) {
			playerService.insertPlayer(player);
			rm.setState(1);
			rm.setMessage("????????????");
		} else {
			rm.setState(0);
			rm.setMessage("???????????????");
		}
		player.setSession(session);
		String retMsg = JsonUtils.toJson(rm, RetMsg.class);
		TexasUtil.sendMsgToOne(player, retMsg);
	}

	@Override
	public void betChips(Session session, String message) {

		BetPlayer bp = JsonUtils.fromJson(message, BetPlayer.class);
		Player p = TexasUtil.getPlayerBySessionId(session.getId());
		// ?????????????????????????????????
		int chip = bp.getInChips();

		if (p != null && p.getRoom() != null) {
			p.getRoom().betchipIn(p, chip, true);
		}
	}

	@Override
	public void fold(Session session, String message) {
		// ?????? Fold?????????????????????????????????????????????????????????
		Player p = TexasUtil.getPlayerBySessionId(session.getId());
		if (p != null && p.getRoom() != null) {
			p.getRoom().fold(p);
		}
	}

	@Override
	public void check(Session session, String message) {
		// ??????
		// Check????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		Player p = TexasUtil.getPlayerBySessionId(session.getId());
		if (p != null && p.getRoom() != null) {
			p.getRoom().check(p, true);
		}
	}

	@Override
	public void standUp(Session session, String message) {
		// ??????

	}

	@Override
	public void sitDown(Session session, String message) {
		// ??????

	}

}
