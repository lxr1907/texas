package com.lxrtalk.texas.service;

import java.util.List;

import jakarta.websocket.Session;

import com.lxrtalk.texas.entity.PageEntity;
import com.lxrtalk.texas.entity.Player;
/**
 * 玩家业务逻辑
 * @author Ming
 *
 */
public interface PlayerService {
	public List<Player> selectPlayers(Player player);

	public List<Player> selectLoginPlayer(Player player);

	public String selectPlayersCount(Player player);

	public void insertPlayer(Player player);

	public void updatePlayer(Player player);

	public Player selectPlayer(Player player);

	public List<Player> selectPlayersPage(Player player, PageEntity page);
	/**
	 * 玩家登录
	 * @param session
	 * @param message
	 * @return
	 */
	public void login(Session session,String message);
	/**
	 * 玩家注册
	 * @param session
	 * @param message
	 * @return
	 */
	public void register(Session session,String message);
	/**
	 * 下注
	 * @param session
	 * @param message
	 */
	public void betChips(Session session,String message);
	/**
	 * 弃牌（玩家主动弃牌）
	 * @param session
	 * @param message
	 */
	public void fold(Session session,String message);
	/**
	 * 过牌
	 * @param session
	 * @param message
	 */
	public void check(Session session,String message);
	/**
	 * 站起
	 * @param session
	 * @param message
	 */
	public void standUp(Session session,String message);
	/**
	 * 坐下
	 * @param session
	 * @param message
	 */
	public void sitDown(Session session,String message);

}
