package yuelj.service;

import java.util.List;

import yuelj.entity.GameLog;

public interface GameLogService {

	List<GameLog> selectGameLog(GameLog entity);

	void insertGameLog(GameLog entity);

}
