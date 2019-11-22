package yuelj.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yuelj.dao.UserMachineDao;
import yuelj.entity.UserMachineEntity;
import yuelj.service.UserMachineService;

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
