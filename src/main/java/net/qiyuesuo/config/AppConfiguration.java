package net.qiyuesuo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {"net.qiyuesuo"})
@Import({MvcConfiguration.class, ServiceConfiguration.class})
/* loaded from: fileserver-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/net/qiyuesuo/config/AppConfiguration.class */
public class AppConfiguration {
}
