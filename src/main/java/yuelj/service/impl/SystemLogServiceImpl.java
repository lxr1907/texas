package yuelj.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yuelj.dao.SystemLogDao;
import yuelj.entity.SystemLogEntity;
import yuelj.service.SystemLogService;

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
