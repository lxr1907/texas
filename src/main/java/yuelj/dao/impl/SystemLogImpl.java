package yuelj.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import yuelj.dao.BaseDao;
import yuelj.dao.SystemLogDao;
import yuelj.entity.SystemLogEntity;

@Repository
public class SystemLogImpl extends BaseDao implements SystemLogDao {

	public  List<SystemLogEntity> selectSystemLog(SystemLogEntity entity) {
		List<SystemLogEntity> list = new ArrayList<SystemLogEntity>();
		list = selectList("LogMapper.selectSystemLog", entity);
		return list;
	}

	public void insertSystemLog(SystemLogEntity entity) {
		insertEntity("LogMapper.insertSystemLog", entity);
	}

}
