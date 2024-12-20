package com.lxrtalk.texas.dao;

import java.util.List;

import com.lxrtalk.texas.entity.GameLog;

public interface GameLogDao {

	List<GameLog> selectGameLog(GameLog entity);

	void insertGameLog(GameLog entity);

}
