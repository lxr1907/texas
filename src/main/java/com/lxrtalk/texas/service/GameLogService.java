package com.lxrtalk.texas.service;

import java.util.List;

import com.lxrtalk.texas.entity.GameLog;

public interface GameLogService {

	List<GameLog> selectGameLog(GameLog entity);

	void insertGameLog(GameLog entity);

}
