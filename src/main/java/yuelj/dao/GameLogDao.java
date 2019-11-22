package yuelj.dao;

import java.util.List;

import yuelj.entity.GameLog;

public interface GameLogDao {

	List<GameLog> selectGameLog(GameLog entity);

	void insertGameLog(GameLog entity);

}
