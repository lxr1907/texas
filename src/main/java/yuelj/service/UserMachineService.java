package yuelj.service;

import java.util.List;

import yuelj.entity.UserMachineEntity;

public interface UserMachineService {

	void addUserMachine(UserMachineEntity info);

	void updateUserMachine(UserMachineEntity info);

	List<UserMachineEntity> queryUserMachine(UserMachineEntity info);

}
