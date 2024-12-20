package com.lxrtalk.texas.service.impl;

import java.util.List;

import jakarta.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxrtalk.texas.dao.PlayerDao;
import com.lxrtalk.texas.entity.BetPlayer;
import com.lxrtalk.texas.entity.PageEntity;
import com.lxrtalk.texas.entity.Player;
import com.lxrtalk.texas.entity.RetMsg;
import com.lxrtalk.texas.service.PlayerService;
import com.lxrtalk.texas.texas.TexasStatic;
import com.lxrtalk.texas.texas.TexasUtil;
import com.lxrtalk.texas.utils.SpringUtil;
import com.lxrtalk.texas.utils.md5encrypt.Md5;
import com.lxrtalk.texas.utils.serialize.JsonUtils;

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
			rm.setMessage("登录失败");
		} else {
			rm.setState(1);
			rm.setMessage(JsonUtils.toJson(currPlayer, Player.class));
			// 保存玩家sessionId，以便给玩家发送消息
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
			rm.setMessage("注册成功");
		} else {
			rm.setState(0);
			rm.setMessage("用户已存在");
		}
		player.setSession(session);
		String retMsg = JsonUtils.toJson(rm, RetMsg.class);
		TexasUtil.sendMsgToOne(player, retMsg);
	}

	@Override
	public void betChips(Session session, String message) {

		BetPlayer bp = JsonUtils.fromJson(message, BetPlayer.class);
		Player p = TexasUtil.getPlayerBySessionId(session.getId());
		// 玩家本次操作所下的筹码
		int chip = bp.getInChips();

		if (p != null && p.getRoom() != null) {
			p.getRoom().betchipIn(p, chip, true);
		}
	}

	@Override
	public void fold(Session session, String message) {
		// 弃牌 Fold，放弃本局游戏，并放弃所有已下的筹码。
		Player p = TexasUtil.getPlayerBySessionId(session.getId());
		if (p != null && p.getRoom() != null) {
			p.getRoom().fold(p);
		}
	}

	@Override
	public void check(Session session, String message) {
		// 过牌
		// Check，不做任何操作过牌到下一个人，并保留下注的权利。过牌必须是在无需跟注的情况下使用，比如前面所有玩家都过牌或弃牌的情况。若前面的玩家一旦有人有下则不允许使用过牌。
		Player p = TexasUtil.getPlayerBySessionId(session.getId());
		if (p != null && p.getRoom() != null) {
			p.getRoom().check(p, true);
		}
	}

	@Override
	public void standUp(Session session, String message) {
		// 站起

	}

	@Override
	public void sitDown(Session session, String message) {
		// 坐下

	}

}
