package com.chaos.springboot.config;

import com.chaos.springboot.dto.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;


@Configuration
@ConditionalOnProperty(
        value = "spring.redis.cluster.enabled",
        havingValue = "true"
)
public class ClusterRedisConfiguration {

    @Value("${spring.redis.cluster.nodes}")
    private List<String> NODES;


    /**
     * Creates new redisTemplate for clusters and sets default configuration
     *
     * @return new redisTemplate
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, ProductDto> redisTemplate() {
        final RedisTemplate<String, ProductDto> template = new RedisTemplate<String, ProductDto>();
        template
                .setKeySerializer(new StringRedisSerializer());

        template.
                setHashKeySerializer(new GenericToStringSerializer<String>(String.class));
        template.
                setHashValueSerializer(
                        new JdkSerializationRedisSerializer()
                );
        template.setValueSerializer(
                new JdkSerializationRedisSerializer()
        );
        RedisClusterConfiguration configuration = new RedisClusterConfiguration(NODES);
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().build();
        JedisConnectionFactory factory = new JedisConnectionFactory(configuration, jedisClientConfiguration);
        factory.afterPropertiesSet();
        template.setConnectionFactory(factory);
        return template;
    }


}
