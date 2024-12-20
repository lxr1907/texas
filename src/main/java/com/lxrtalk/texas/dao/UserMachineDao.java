package com.lxrtalk.texas.dao;

import java.util.List;

import com.lxrtalk.texas.entity.UserMachineEntity;

public interface UserMachineDao {

	void addUserMachine(UserMachineEntity UserMachine);

	void updateUserMachine(UserMachineEntity UserMachine);

	 List<UserMachineEntity> getUserMachine(UserMachineEntity UserMachine);

}
