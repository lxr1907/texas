package yuelj.dao;

import java.util.List;

import yuelj.entity.PageEntity;
import yuelj.entity.Player;

public interface PlayerDao {
	public List<Player> selectPlayers(Player player);
	public List<Player> selectLoginPlayer(Player player);
	public String selectPlayersCount(Player player) ;
	public void insertPlayer(Player player);

	public void updatePlayer(Player player);


	public void updatePlayerPassword(Player player);
	public List<Player> selectPlayersPage(Player player, PageEntity page);
	
}
