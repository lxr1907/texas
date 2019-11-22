package yuelj.service;

import java.util.List;

import yuelj.entity.PageEntity;
import yuelj.entity.UserEntity;

public interface UserService {
	public List<UserEntity> selectUsers(UserEntity user);

	public UserEntity selectLoginUser(UserEntity user);

	public String selectUsersCount(UserEntity user);

	public void insertUser(UserEntity user);

	public void updateUser(UserEntity user);

	public void updateUserState(UserEntity user);

	public UserEntity selectUser(UserEntity user);

	public List<UserEntity> selectUsersPage(UserEntity user, PageEntity page);

	/**
	 * 检查账号和手机号是否重复
	 * 
	 * @param user
	 */
	UserEntity checkAccountAndMobile(UserEntity user);

	/**
	 * 更新密码
	 * 
	 * @param user
	 */
	void updateUserPassword(UserEntity user);

	/**
	 * 冻结用户
	 * 
	 * @param user
	 */
	void freezeUser(UserEntity user);

	/**
	 * 增加用户被赞数
	 * 
	 * @param user
	 * @param plus
	 */
	void plusUserFollow(UserEntity user, int plus);

}
