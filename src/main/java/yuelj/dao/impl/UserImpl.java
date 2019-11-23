package yuelj.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import yuelj.dao.UserDao;
import yuelj.dao.base.BaseDao;
import yuelj.entity.PageEntity;
import yuelj.entity.UserEntity;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import yuelj.dao.SystemLogDao;
import yuelj.dao.base.BaseDao;
import yuelj.entity.SystemLogEntity;
@Primary
@Repository
public class UserImpl extends BaseDao implements UserDao {

	@Override
	public List<UserEntity> selectUsers(UserEntity user) {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list = selectList("UserMapper.selectUser", user);
		return list;
	}

	@Override
	public List<UserEntity> selectLoginUser(UserEntity user) {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list = selectList("UserMapper.selectLoginUser", user);
		return list;
	}

	@Override
	public String selectUsersCount(UserEntity user) {
		String count = this.getSqlSession().selectOne("UserMapper.selectUserCount", user);
		return count;
	}

	@Override
	public void insertUser(UserEntity user) {
		insertEntity("UserMapper.insertUser", user);
	}

	@Override
	public void updateUser(UserEntity user) {
		updateEntity("UserMapper.updateUser", user);
	}

	@Override
	public void updateUserCache(UserEntity user) {
		updateEntityCache("UserMapper.cache", user);
	}

	@Override
	public void updateUserState(UserEntity user) {
		updateEntity("UserMapper.updateUserState", user);
	}

	@Override
	public void updateUserPassword(UserEntity user) {
		updateEntity("UserMapper.updateUserPassword", user);
	}

	@Override
	public List<UserEntity> selectUsersPage(UserEntity user, PageEntity page) {
		List<UserEntity> list = new ArrayList<UserEntity>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user", user);
		map.put("page", page);
		String count = this.getSqlSession().selectOne("UserMapper.selectUserCount", user);
		page.setTotalCount(count);
		list = this.getSqlSession().selectList("UserMapper.selectUserPage", map);
		return list;
	}

	@Override
	public UserEntity checkAccountOrMobile(UserEntity user) {
		List<UserEntity> list = this.getSqlSession().selectList("UserMapper.checkAccountAndMobile", user);
		if (null != list && list.size() > 0) {
			return (UserEntity) list.get(0);
		}
		return null;
	}
}
