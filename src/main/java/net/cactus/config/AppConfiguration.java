package net.cactus.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {"net.cactus"})
@Import({MvcConfiguration.class, ServiceConfiguration.class})
public class AppConfiguration {
}
