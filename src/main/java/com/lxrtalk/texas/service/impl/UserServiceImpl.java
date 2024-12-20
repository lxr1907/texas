package com.lxrtalk.texas.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxrtalk.texas.dao.UserDao;
import com.lxrtalk.texas.entity.PageEntity;
import com.lxrtalk.texas.entity.UserEntity;
import com.lxrtalk.texas.service.UserService;
import com.lxrtalk.texas.utils.SensitiveWord.TabooedUtils;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userDao;

	public List<UserEntity> selectUsers(UserEntity user) {
		List<UserEntity> list = this.userDao.selectUsers(user);
		clearPassword(list);
		return list;

	}

	public UserEntity selectLoginUser(UserEntity user) {
		List<UserEntity> list = this.userDao.selectLoginUser(user);
		if (list != null && list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public String selectUsersCount(UserEntity user) {
		String list = this.userDao.selectUsersCount(user);
		return list;
	}

	public UserEntity selectUser(UserEntity user) {
		List<UserEntity> list = this.userDao.selectUsers(user);
		if (list != null && list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public void updateUser(UserEntity user) {

		if (user.getName() != null && user.getName().length() != 0) {
			// 敏感词过滤
			String name = TabooedUtils.getTabooedWords(user.getName(), "**");
			user.setName(name);
		}
		this.userDao.updateUser(user);
	}

	public void updateUserState(UserEntity user) {
		this.userDao.updateUserState(user);
	}

	public void insertUser(UserEntity user) {
		if (user.getName() != null && user.getName().length() != 0) {
			// 敏感词过滤
			String name = TabooedUtils.getTabooedWords(user.getName(), "**");
			user.setName(name);
		}
		this.userDao.insertUser(user);
	}

	@Override
	public List<UserEntity> selectUsersPage(UserEntity user, PageEntity page) {
		List<UserEntity> list = this.userDao.selectUsersPage(user, page);
		clearPassword(list);
		return list;
	}

	private void clearPassword(List<UserEntity> list) {
		for (UserEntity u : list) {
			u.setPassword(null);
		}
	}

	@Override
	public void plusUserFollow(UserEntity user, int plus) {
		List<UserEntity> list = this.userDao.selectUsers(user);
		if (list != null && list.size() != 0) {
			int count = 0;
			if (list.get(0).getLikecount() == null
					|| list.get(0).getLikecount().length() == 0) {

			} else {
				count = Integer.parseInt(list.get(0).getLikecount()) + plus;
			}
			if (count >= 0) {
				list.get(0).setLikecount(count + "");
			}
		}

	}

	@Override
	public UserEntity checkAccountAndMobile(UserEntity user) {
		return userDao.checkAccountOrMobile(user);
	}

	@Override
	public void updateUserPassword(UserEntity user) {
		userDao.updateUserPassword(user);
	}

	@Override
	public void freezeUser(UserEntity user) {
		if (user.getId() != null && user.getId().length() != 0) {
			user.setStatus("2");
			userDao.updateUserState(user);
			
		}
	}

}
