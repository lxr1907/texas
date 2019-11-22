package yuelj.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import yuelj.dao.BaseDao;
import yuelj.dao.UserMachineDao;
import yuelj.entity.UserMachineEntity;

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
