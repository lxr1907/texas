package com.lxrtalk.texas.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxrtalk.texas.dao.SystemLogDao;
import com.lxrtalk.texas.entity.SystemLogEntity;
import com.lxrtalk.texas.service.SystemLogService;

@Service("SystemLogServiceImpl")
public class SystemLogServiceImpl implements SystemLogService {
	@Autowired
	private SystemLogDao dao;

	public List<SystemLogEntity> selectSystemLog(SystemLogEntity entity) {
		List<SystemLogEntity> list = this.dao.selectSystemLog(entity);
		return list;
	}

	public void insertSystemLog(SystemLogEntity entity) {
		this.dao.insertSystemLog(entity);
	}
}
