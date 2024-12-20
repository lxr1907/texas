package com.lxrtalk.texas.service;

import java.util.List;

import com.lxrtalk.texas.entity.SystemLogEntity;

public interface SystemLogService {

	List<SystemLogEntity> selectSystemLog(SystemLogEntity entity);

	void insertSystemLog(SystemLogEntity entity);

}
