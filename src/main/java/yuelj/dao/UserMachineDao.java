package yuelj.dao;

import java.util.List;

import yuelj.entity.UserMachineEntity;

public interface UserMachineDao {

	void addUserMachine(UserMachineEntity UserMachine);

	void updateUserMachine(UserMachineEntity UserMachine);

	 List<UserMachineEntity> getUserMachine(UserMachineEntity UserMachine);

}
