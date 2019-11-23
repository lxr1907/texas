package yuelj.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import yuelj.dao.UserMachineDao;
import yuelj.dao.base.BaseDao;
import yuelj.entity.UserMachineEntity;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import yuelj.dao.SystemLogDao;
import yuelj.dao.base.BaseDao;
import yuelj.entity.SystemLogEntity;
@Primary
@Repository
public class UserMachineDaoImpl extends BaseDao implements UserMachineDao {
	@Override
	public void addUserMachine(UserMachineEntity UserMachine) {
		this.getSqlSession().insert("UserMachineMapper.addUserMachine",
				UserMachine);
	}

	@Override
	public void updateUserMachine(UserMachineEntity UserMachine) {
		this.getSqlSession().update("UserMachineMapper.updateUserMachine",
				UserMachine);
	}

	@Override
	public List<UserMachineEntity> getUserMachine(UserMachineEntity UserMachine) {
		return this.getSqlSession().selectList(
				"UserMachineMapper.queryUserMachine", UserMachine);
	}
}
