package yuelj.dao;

import java.util.List;

import yuelj.entity.SystemLogEntity;

public interface SystemLogDao {

	List<SystemLogEntity> selectSystemLog(SystemLogEntity entity);

	void insertSystemLog(SystemLogEntity entity);

}
