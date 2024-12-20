package com.lxrtalk.texas.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxrtalk.texas.dao.GameLogDao;
import com.lxrtalk.texas.entity.GameLog;
import com.lxrtalk.texas.service.GameLogService;

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
