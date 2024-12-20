package com.lxrtalk.texas.utils.cache;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;
import com.lxrtalk.texas.constants.ParamsAndURL;

public class MemCacheOcsUtils {
	private static Logger logger = LogManager.getLogger(MemCacheOcsUtils.class);
	public static final String host = (String) ParamsAndURL.getParam("MEMCACHE_host");
	public static final String port = "11211"; // 默认端口 11211，不用改
	public static MemcachedClient cache;

	public static MemcachedClient getCache() {
		if (cache == null) {
			try {
				cache = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses(host + ":" + port));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return cache;
	}

	/**
	 * 设置缓存
	 * 
	 * @param key
	 * @param value
	 */
	public static void setData(String key, String value) {
		try {
			cache = getCache();
			// 执行set操作，向缓存中存数据
			cache.set(key, 60 * 60 * 24 * 30, value);
			String logstr = value;
			if (value.length() >= 100) {
				logstr = value.substring(0, 100);
			}
			logger.error("memcached set key:" + key + ",value:" + logstr);
		} catch (Exception e) {
			logger.error("memcached error set key:" + key + ",value:" + value);
			e.printStackTrace();
			cache = null;
		}
	}

	/**
	 * 设置带有过期时间的缓存
	 * 
	 * @param key
	 * @param value
	 * @param time
	 *            过期时间距离当前时间的时长，大于30天变为过期时间戳，秒
	 */
	public static void setDataTime(String key, String value, int time) {
		// Date d = new Date();
		if (time > 60 * 60 * 24 * 30) {
			// time = time + (int) d.getTime() / 1000;
			time = 60 * 60 * 24 * 30;
		}
		try {
			// cache = getCache();
			// 执行set操作，向缓存中存数据
			// cache.set(key, time, value);
			// log打印
			// String logstr = value;
			// if (value.length() >= 100) {
			// logstr = value.substring(0, 100);
			// }
			// logger.error("memcached set key:" + key + ",value:" +
			// logstr + "time:" + time);
		} catch (Exception e) {
			logger.error("memcached error set key:" + key + ",value:" + value);
			e.printStackTrace();
			cache = null;
		}
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 * @return
	 */
	public static String getData(String key) {
		String value = null;
		logger.error("from memcachekey:" + key);
		try {
			cache = getCache();
			// 执行get操作，从缓存中读数据,读取key为"ocs"的数据
			Object ob = cache.get(key);
			if (ob != null) {
				value = ob.toString();
				int length = value.length();
				if (length > 100) {
					length = 100;
				}
				// logger.error("Hit from memcache key:" + key + "val:" +
				// value.substring(0, length));
			}
		} catch (Exception e) {
			cache.set(key, 0, "");
			e.printStackTrace();
			cache = null;
			return null;
		}
		return value;
	}

	/**
	 * 测试代码
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		MemcachedClient cache = null;
		try {

			cache = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses(host + ":" + port));

			// 向OCS中存若干个数据，随后可以在OCS控制台监控上看到统计信息
			for (int i = 0; i < 100; i++) {
				String key = "key-" + i;
				String value = "value-" + i;

				// 执行set操作，向缓存中存数据
				cache.set(key, 1000, value);
			}

			// 执行get操作，从缓存中读数据,读取key为"ocs"的数据
			logger.error("Get memcache:" + cache.get("key-1"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cache != null) {
			cache.shutdown();
		}

	}
}
