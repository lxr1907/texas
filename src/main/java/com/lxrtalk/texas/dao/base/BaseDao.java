package com.lxrtalk.texas.dao.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;

import com.lxrtalk.texas.entity.BaseEntity;
import com.lxrtalk.texas.utils.cache.MemCacheOcsUtils;
import com.lxrtalk.texas.utils.serialize.JsonUtils;

public class BaseDao extends SqlSessionDaoSupport {
	@Autowired
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		super.setSqlSessionFactory(sqlSessionFactory);
	}

	public final static boolean useMemcache = false;

	@SuppressWarnings("unchecked")
	public <T extends BaseEntity> List<T> selectList(String sqlmap, T ob) {
		List<T> list = new ArrayList<T>();
		SqlSession session = getSqlSession();
		T be = (T) ob;
		if (ob != null && be.getId() != null && useMemcache) {
			// 从缓存中获取
			String json = MemCacheOcsUtils.getData(sqlmap.split("\\.")[0] + be.getId());
			if (json == null || json.length() == 0) {
				list = session.selectList(sqlmap, ob);

				if (list != null && list.size() == 1) {
					MemCacheOcsUtils.setData(sqlmap.split("\\.")[0] + be.getId(),
							JsonUtils.toJson(list.get(0), ob.getClass()));
				}
			} else {
				ob = (T) JsonUtils.fromJson(json, ob.getClass());
				list.add(ob);
			}
		} else {
			list = session.selectList(sqlmap, ob);
		}
		return list;
	}

	/**
	 * 只更新entity部分信息，为空的不变
	 * 
	 * @param sqlmap
	 * @param ob
	 */
	public <T extends BaseEntity> void updateEntity(String sqlmap, T ob) {
		SqlSession session = getSqlSession();
		session.update(sqlmap, ob);
		delCache(sqlmap, ob);
	}

	public <T extends BaseEntity> void updateEntityCache(String sqlmap, T ob) {
		updateCache(sqlmap, ob);
	}

	public <T> void getEntityCache(String sqlmap, T ob) {
		getCache(sqlmap, ob);
	}

	public <T extends BaseEntity> String insertEntity(String sqlmap, T ob) {
		SqlSession session = getSqlSession();
		String ret = session.insert(sqlmap, ob) + "";
		return ret;
	}

	public void deleteEntity(String sqlmap, String id) {
		SqlSession session = getSqlSession();
		session.delete(sqlmap, Integer.parseInt(id));
		delCache(sqlmap, id);
	}

	private static <T extends BaseEntity> void updateCache(String sqlmap, T be) {
		if (be.getId() != null && useMemcache) {
			// 更新缓存
			String value = JsonUtils.toJson(be, be.getClass());
			MemCacheOcsUtils.setData(sqlmap.split("\\.")[0] + be.getId(), value);
		}
	}

	private static <T> void getCache(String sqlmap, T ob) {
		BaseEntity be = (BaseEntity) ob;
		if (be.getId() != null && useMemcache) {
			String json = MemCacheOcsUtils.getData(sqlmap.split("\\.")[0] + be.getId());
			ob = (T) JsonUtils.fromJson(json, ob.getClass());
		}
	}

	private static void delCache(String sqlmap, String id) {
		if (useMemcache)
			// 删除缓存
			MemCacheOcsUtils.setData(sqlmap.split("\\.")[0] + id, "");
	}

	private static <T extends BaseEntity> void delCache(String sqlmap, T be) {
		if (be.getId() != null && useMemcache) {
			delCache(sqlmap, be.getId());
		}
	}

}
