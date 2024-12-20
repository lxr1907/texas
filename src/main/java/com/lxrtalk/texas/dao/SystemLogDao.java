package com.lxrtalk.texas.dao;

import java.util.List;

import com.lxrtalk.texas.entity.SystemLogEntity;

public interface SystemLogDao {

	List<SystemLogEntity> selectSystemLog(SystemLogEntity entity);

	void insertSystemLog(SystemLogEntity entity);

}
