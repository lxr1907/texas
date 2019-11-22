package yuelj.constants;

import java.lang.reflect.Field;

import yuelj.utils.logs.SystemLog;

public class ParamsAndURL {
	/**
	 * 设置当前所在的环境ONLINE线上，TEST测试环境
	 */
	public static final String TEST_OR_ONLINE = "TEST";
	// 阿里云盾绿网
	public static final String Green_accessKeyId_TEST = "LTAIvF9OqJPOFFHP";
	public static final String Green_accessKeySecret_TEST = "3KlcIX6WktR1REaUJuTAD0sGVyvszL";

	// OSS存储
	public static final String OSSUpload_accessKeyId_TEST = "qeRpZl7hhtewzOHI";
	public static final String OSSUpload_accessKeySecret_TEST = "FTzKszkMldOrrFcMdt3YhLKDX9rztB";
	public static final String OSSUpload_endpoint_TEST = "http://oss-cn-hangzhou.aliyuncs.com";
	public static final String OSSUpload_IMG_BUCKET_TEST = "fdjimg";
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
		SystemLog.printlog(getParam("WSTOCK_ACC"));
	}
}
