package com.lxrtalk.texas.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxrtalk.texas.dao.UserMachineDao;
import com.lxrtalk.texas.entity.UserMachineEntity;
import com.lxrtalk.texas.service.UserMachineService;

@Service("UserMachineService")
public class UserMachineServiceImpl implements UserMachineService {
	@Autowired
	private UserMachineDao userMachineDao;

	@Override
	public void addUserMachine(UserMachineEntity info) {
		userMachineDao.addUserMachine(info);
	}

	@Override
	public void updateUserMachine(UserMachineEntity info) {
		userMachineDao.updateUserMachine(info);
	}

	@Override
	public List<UserMachineEntity> queryUserMachine(UserMachineEntity info) {
		List<UserMachineEntity> cList = userMachineDao.getUserMachine(info);
		return cList;
	}
}
