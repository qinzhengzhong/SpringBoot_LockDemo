package com.allan.lockdemo.redis;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 
 * @ClassName: RedisCacheConfig
 * @Description: Redis工具类
 * @author qinzz
 * @date 2018年8月22日
 *
 */
@Configuration
@EnableAutoConfiguration
public class RedisCacheConfig extends CachingConfigurerSupport {

	@Value("${spring.redis.host}")
	private String host;// 地址

	@Value("${spring.redis.port}")
	private int port;// 端口

	@Value("${spring.redis.timeout}")
	private int timeout;// 超时时间

	@Value("${spring.redis.database}")
	private int database;

	@Value("${spring.redis.password}")
	private String password;// 密码

//	@Value("${spring.redis.sentinel.nodes}")
//	private String redisNodes;// 哨兵节点
//
//	@Value("${spring.redis.sentinel.master}")
//	private String master; // 哨兵模式猪节点
//
//	@Value("${spring.redis.cluster.nodes}")
//	private String clusterNodes; // 分片集群节点
//
//	@Value("${spring.redis.cluster.max-redirects}")
//	private int maxRedirects;// 在群集中执行命令时要遵循的最大重定向数目

	/**
	 * 
	 * @Title: redisSentinelConfiguration
	 * @Description: redis哨兵配置(没有搭建哨兵模式可以不用)
	 * @param @return
	 * @author qinzz
	 * @date 2018年8月22日
	 * @return
	 */
//	@Bean
//	public RedisSentinelConfiguration redisSentinelConfiguration() {
//		RedisSentinelConfiguration configuration = new RedisSentinelConfiguration();
//		String[] host = redisNodes.split(",");
//		for (String redisHost : host) {
//			String[] item = redisHost.split(":");
//			String ip = item[0];
//			String port = item[1];
//			configuration.addSentinel(new RedisNode(ip, Integer.parseInt(port)));
//		}
//		configuration.setMaster(master);
//		return configuration;
//	}

	/**
	 * 
	 * @Title: redisClusterConfiguration
	 * @Description: 读取redis分片集群配置(没有搭建分片集群可以不用)
	 * @param @return
	 * @author qinzz
	 * @date 2018年8月29日
	 * @return
	 */
//	@Bean
//	public RedisClusterConfiguration redisClusterConfiguration() {
//		RedisClusterConfiguration clusterConfiguration = clusterConfiguration = new RedisClusterConfiguration();
//		String[] hosts = clusterNodes.split(",");
//		for (String host : hosts) {
//			String[] item = host.split(":");
//			String ip = item[0];
//			String port = item[1];
//			clusterConfiguration.addClusterNode(new RedisNode(ip, Integer.parseInt(port)));
//		}
//		clusterConfiguration.setMaxRedirects(maxRedirects);
//		return clusterConfiguration;
//	}

	/**
	 * 
	 * @Title: jedisConnectionFactory
	 * @Description: 连接redis的工厂类
	 * @param @return
	 * @author qinzz
	 * @date 2018年8月22日
	 * @return
	 */
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		// JedisConnectionFactory factory = new JedisConnectionFactory(redisSentinelConfiguration());//搭建哨兵模式时使用
		JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setHostName(host);
		factory.setPort(port);
		factory.setTimeout(timeout);
		factory.setPassword(password);
		factory.setDatabase(database);
		return factory;
	}

	/**
	 * 
	 * @Title: redisTemplate
	 * @Description:<br>
	 * 					配置RedisTemplate 设置添加序列化器 key 使用string序列化器 value 使用Json序列化器
	 *                   <br>
	 *                   还有一种简答的设置方式，改变defaultSerializer对象的实现。
	 * @param @return
	 * @author qinzz
	 * @date 2018年8月22日
	 * @return
	 */
	@Bean
	public RedisTemplate<Object, Object> redisTemplate() {
		// StringRedisTemplate的构造方法中默认设置了stringSerializer
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		/**
		 * Redis事务：
		 * MULTI:该命令用于开启一个事务；之后用户可以输入多行命令，都会被保存起来等待执行。<br>
		 * EXEC:执行保存起来的事务内的所有命令，该命令执行后无法回滚，因此执行速度也较快。<br>
		 * DISCARD:该命令表示放弃事务，从当前的事务中退出。以免影响后续的语句执行。<br>
		 * WATCH：该命令可以为Redis事务提供check-and-set行为。被watch监视的键，如果有至少一个在执行exec前被改动，则整个事务会被取消执行。
		 * <br>
		 * 注：事务中遇到的错误一般可以分为exec执行前和执行后；
		 *   执行前如果出现错误，继续执行exec，操作语句将不会被执行；<br>
		 *   如果执行exec后出现错误，将跳过错误语句继续执行后面的语句。如果事务在exec之前连接被断开，则系统会自动清除事务；<br>
		 *   如果执行exec后连接被断开，也无需担心，因为所有语句都保存在队列中<br>
		 *   watch仅作用于紧接着的一个事务。如果想放弃监视可以使用unwatch。
		 */
		//设置开启事务(redis 使用MULTI...EXEC|DISCARD 来完成事务操作)
		template.setEnableTransactionSupport(true);
		// set key serializer
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		template.setKeySerializer(stringRedisSerializer);
		template.setHashKeySerializer(stringRedisSerializer);

		template.setConnectionFactory(jedisConnectionFactory());
		template.afterPropertiesSet();
		return template;
	}

	/**
	 * 设置RedisCacheManager 使用cache注解管理redis缓存
	 */
	@Override
	@Bean
	public RedisCacheManager cacheManager() {
		RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate());
		return redisCacheManager;
	}

	/**
	 * 自定义生成redis-key
	 */
	@Override
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object o, Method method, Object... objects) {
				StringBuilder sb = new StringBuilder();
				sb.append(o.getClass().getName()).append(".");
				sb.append(method.getName()).append(".");
				for (Object obj : objects) {
					sb.append(obj.toString());
				}
				System.out.println("keyGenerator=" + sb.toString());
				return sb.toString();
			}
		};
	}
}
