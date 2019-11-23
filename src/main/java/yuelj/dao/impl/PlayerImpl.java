package yuelj.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import yuelj.dao.PlayerDao;
import yuelj.dao.base.BaseDao;
import yuelj.entity.PageEntity;
import yuelj.entity.Player;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import yuelj.dao.SystemLogDao;
import yuelj.dao.base.BaseDao;
import yuelj.entity.SystemLogEntity;
@Primary
@Repository
public class PlayerImpl extends BaseDao implements PlayerDao {

	@Override
	public List<Player> selectPlayers(Player player) {
		List<Player> list = new ArrayList<Player>();
		list = selectList("PlayerMapper.selectPlayer", player);
		return list;
	}

	@Override
	public List<Player> selectLoginPlayer(Player player) {
		List<Player> list = new ArrayList<Player>();
		list = selectList("PlayerMapper.selectLoginPlayer", player);
		return list;
	}

	@Override
	public String selectPlayersCount(Player player) {
		String count = this.getSqlSession().selectOne("PlayerMapper.selectPlayerCount", player);
		return count;
	}

	@Override
	public void insertPlayer(Player player) {
		insertEntity("PlayerMapper.insertPlayer", player);
	}

	@Override
	public void updatePlayer(Player player) {
		updateEntity("PlayerMapper.updatePlayer", player);
	}

	@Override
	public void updatePlayerCache(Player player) {
		updateEntityCache("PlayerMapper.cache", player);
	}

	@Override
	public void updatePlayerPassword(Player player) {
		updateEntity("PlayerMapper.updatePlayerPassword", player);
	}

	@Override
	public List<Player> selectPlayersPage(Player player, PageEntity page) {
		List<Player> list = new ArrayList<Player>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Player", player);
		map.put("page", page);
		String count = this.getSqlSession().selectOne("PlayerMapper.selectPlayerCount", player);
		page.setTotalCount(count);
		list = this.getSqlSession().selectList("PlayerMapper.selectPlayerPage", map);
		return list;
	}

}
