package com.lxrtalk.texas.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.lxrtalk.texas.dao.GameLogDao;
import com.lxrtalk.texas.dao.base.BaseDao;
import com.lxrtalk.texas.entity.GameLog;

import org.springframework.context.annotation.Primary;

@Primary
@Repository
public class GameLogDaoImpl extends BaseDao implements GameLogDao {

	@Override
	public List<GameLog> selectGameLog(GameLog entity) {
		List<GameLog> list = new ArrayList<GameLog>();
		list = selectList("GameLogMapper.selectGameLog", entity);
		return list;
	}

	@Override
	public void insertGameLog(GameLog entity) {
		insertEntity("GameLogMapper.insertGameLog", entity);
	}

}
