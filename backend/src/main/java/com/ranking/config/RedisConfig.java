package com.ranking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis config
 */
@Configuration
public class RedisConfig {

	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}

	/**
	 * 조회수(ZINCRBY)를 원자적으로 처리하는 Lua 스크립트
	 * resources/scripts/increase-view-count.lua 를 로드해서 EVAL로 실행한다
	 */
	@Bean
	public RedisScript<String> increaseViewCountScript() {
		DefaultRedisScript<String> script = new DefaultRedisScript<>();
		script.setLocation(new ClassPathResource("scripts/increase-view-count.lua"));
		script.setResultType(String.class);
		return script;
	}

}
