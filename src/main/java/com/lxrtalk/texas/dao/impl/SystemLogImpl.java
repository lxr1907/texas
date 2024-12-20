package com.lxrtalk.texas.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.lxrtalk.texas.dao.SystemLogDao;
import com.lxrtalk.texas.dao.base.BaseDao;
import com.lxrtalk.texas.entity.SystemLogEntity;
@Primary
@Repository
public class SystemLogImpl extends BaseDao implements SystemLogDao {

	@Override
	public  List<SystemLogEntity> selectSystemLog(SystemLogEntity entity) {
		List<SystemLogEntity> list = new ArrayList<SystemLogEntity>();
		list = selectList("LogMapper.selectSystemLog", entity);
		return list;
	}

	@Override
	public void insertSystemLog(SystemLogEntity entity) {
		insertEntity("LogMapper.insertSystemLog", entity);
	}

}
