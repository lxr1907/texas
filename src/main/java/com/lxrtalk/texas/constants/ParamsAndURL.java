package com.lxrtalk.texas.constants;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParamsAndURL {
	private static Logger logger = LogManager.getLogger(ParamsAndURL.class);
	/**
	 * 设置当前所在的环境ONLINE线上，TEST测试环境
	 */
	public static final String TEST_OR_ONLINE = "TEST";
	
	// memcache缓存
	public static final String MEMCACHE_host_TEST = "127.0.0.1";
	public static final String MEMCACHE_host_ONLINE = "44478f99146944ea.m.cnhzalicm10pub001.ocs.aliyuncs.com";
	// 是否开启日志
	public static final boolean OPEN_LOG = true;

	/**
	 * 获取连接参数
	 * 
	 * @param key
	 *            参数名
	 * @return
	 */
	public static Object getParam(String key) {
		return getProperty(key + "_" + TEST_OR_ONLINE);
	}

	private static Object getProperty(String key) {
		Object ret = "";
		try {
			Field f = ParamsAndURL.class.getDeclaredField(key);
			// 写数据
			ret = f.get(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public static void main(String[] args) {
		logger.info(getParam("WSTOCK_ACC"));
	}
}
