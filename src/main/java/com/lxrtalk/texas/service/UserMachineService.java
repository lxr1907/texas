package com.lxrtalk.texas.service;

import java.util.List;

import com.lxrtalk.texas.entity.UserMachineEntity;

public interface UserMachineService {

	void addUserMachine(UserMachineEntity info);

	void updateUserMachine(UserMachineEntity info);

	List<UserMachineEntity> queryUserMachine(UserMachineEntity info);

}
