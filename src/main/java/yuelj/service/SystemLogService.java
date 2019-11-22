package yuelj.service;

import java.util.List;

import yuelj.entity.SystemLogEntity;

public interface SystemLogService {

	List<SystemLogEntity> selectSystemLog(SystemLogEntity entity);

	void insertSystemLog(SystemLogEntity entity);

}
