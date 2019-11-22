package yuelj.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import yuelj.dao.BaseDao;
import yuelj.dao.UserDao;
import yuelj.entity.PageEntity;
import yuelj.entity.UserEntity;

@Repository
public class UserImpl extends BaseDao implements UserDao {

	public List<UserEntity> selectUsers(UserEntity user) {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list = selectList("UserMapper.selectUser", user);
		return list;
	}

	public List<UserEntity> selectLoginUser(UserEntity user) {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list = selectList("UserMapper.selectLoginUser", user);
		return list;
	}

	public String selectUsersCount(UserEntity user) {
		String count = this.getSqlSession().selectOne("UserMapper.selectUserCount", user);
		return count;
	}

	public void insertUser(UserEntity user) {
		insertEntity("UserMapper.insertUser", user);
	}

	public void updateUser(UserEntity user) {
		updateEntity("UserMapper.updateUser", user);
	}

	public void updateUserCache(UserEntity user) {
		updateEntityCache("UserMapper.cache", user);
	}

	public void updateUserState(UserEntity user) {
		updateEntity("UserMapper.updateUserState", user);
	}

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
