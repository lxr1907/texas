package yuelj.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yuelj.dao.GameLogDao;
import yuelj.entity.GameLog;
import yuelj.service.GameLogService;

@Service("gameLogService")
public class GameLogServiceImpl implements GameLogService {
	@Autowired
	private GameLogDao dao;

	public List<GameLog> selectGameLog(GameLog entity) {
		List<GameLog> list = this.dao.selectGameLog(entity);
		return list;
	}

	public void insertGameLog(GameLog entity) {
		this.dao.insertGameLog(entity);
	}
}
