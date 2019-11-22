package yuelj.utils.cache;

import java.util.LinkedList;
import java.util.List;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import yuelj.utils.serialize.KryoSerializer;

public class RedisUtils {
	private static ShardedJedisPool pool;
	static {

		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(100);
		config.setMaxIdle(50);
		config.setMaxWaitMillis(3000);
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true);
		// 集群
		JedisShardInfo jedisShardInfo1 = new JedisShardInfo("120.26.217.116", 6379);
		jedisShardInfo1.setPassword("240039080iZ238");
		List<JedisShardInfo> list = new LinkedList<JedisShardInfo>();
		list.add(jedisShardInfo1);
		pool = new ShardedJedisPool(config, list);
	}

	public static ShardedJedis getShardedJedis() {
		try {
			return pool.getResource();
		}
		// 突发问题返回null供handle处理(不直接抛给终端)
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		ShardedJedis jedis = pool.getResource();
		String keys = "name";
		String vaule = jedis.set(keys, "tanggao");
		System.out.println(vaule);
	}

	/**
	 * 设置redis key value值
	 * 
	 * @param <T>
	 * @param key
	 * @param value
	 * @
	 */
	public static <T extends Object> void setKVToRedis(String key, T value) {
		ShardedJedis jedis = null;
		try {
			jedis = getShardedJedis();
			jedis.set(key.getBytes(), new KryoSerializer<T>().serialize(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过key从redis中获取value
	 * 
	 * @param key
	 * @return
	 * @return @
	 */
	public static <T extends Object> T getKVFromRedis(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = getShardedJedis();
			byte[] value = jedis.get(key.getBytes());
			if (value == null) {
				return null;
			}
			return new KryoSerializer<T>().deserialize(value);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 设置redis key value值
	 * 
	 * @param <T>
	 * @param key
	 * @param field
	 * @param value
	 * @
	 */
	public static <T extends Object> void setKVToRedis(String key, String field, T value) {
		ShardedJedis jedis = null;
		try {
			jedis = getShardedJedis();
			jedis.hset(key.getBytes(), field.getBytes(), new KryoSerializer<T>().serialize(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过key从redis中获取value
	 * 
	 * @param key
	 * @param field
	 * @return
	 * @return @
	 */
	public static <T extends Object> T getKVFromRedis(String key, String field) {
		ShardedJedis jedis = null;
		try {
			jedis = getShardedJedis();
			byte[] value = jedis.hget(key.getBytes(), field.getBytes());
			if (value == null) {
				return null;
			}
			return new KryoSerializer<T>().deserialize(value);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
