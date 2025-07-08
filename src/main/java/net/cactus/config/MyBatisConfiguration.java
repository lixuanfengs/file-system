package net.cactus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * MyBatis配置类
 * 只负责导入MyBatis配置，服务Bean由@Service注解自动注册
 *
 * @author FileServer
 * @version 1.0
 */
@ImportResource({"classpath:spring-mybatis.xml"})
@Configuration
public class MyBatisConfiguration {
    // MyBatis配置通过spring-mybatis.xml导入
    // 服务Bean通过@Service注解自动注册，无需手动配置
}
