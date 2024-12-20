package com.lxrtalk.texas.dao;

import java.util.List;

import com.lxrtalk.texas.entity.PageEntity;
import com.lxrtalk.texas.entity.UserEntity;

public interface UserDao {
	public List<UserEntity> selectUsers(UserEntity user);
	public List<UserEntity> selectLoginUser(UserEntity user);
	public String selectUsersCount(UserEntity user) ;
	public void insertUser(UserEntity user);

	public void updateUser(UserEntity user);

	public void updateUserState(UserEntity user);

	public void updateUserPassword(UserEntity user);
	public List<UserEntity> selectUsersPage(UserEntity user, PageEntity page);
	
	/**
	 * 账号和手机号去重
	 * @param user
	 * @return
	 */
	UserEntity checkAccountOrMobile(UserEntity user);
	void updateUserCache(UserEntity user);
}
