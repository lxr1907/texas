package com.lxrtalk.texas.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.lxrtalk.texas.dao.UserMachineDao;
import com.lxrtalk.texas.dao.base.BaseDao;
import com.lxrtalk.texas.entity.UserMachineEntity;

import org.springframework.context.annotation.Primary;

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
