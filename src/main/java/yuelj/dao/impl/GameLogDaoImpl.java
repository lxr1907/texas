package yuelj.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import yuelj.dao.GameLogDao;
import yuelj.dao.base.BaseDao;
import yuelj.entity.GameLog;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import yuelj.dao.SystemLogDao;
import yuelj.dao.base.BaseDao;
import yuelj.entity.SystemLogEntity;
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
