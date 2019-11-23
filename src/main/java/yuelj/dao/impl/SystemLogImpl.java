package yuelj.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import yuelj.dao.SystemLogDao;
import yuelj.dao.base.BaseDao;
import yuelj.entity.SystemLogEntity;
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
