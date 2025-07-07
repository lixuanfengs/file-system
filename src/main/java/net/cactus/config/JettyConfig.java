package net.cactus.config;

import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JettyConfig {

    @Bean
    public WebServerFactoryCustomizer<JettyServletWebServerFactory> jettyCustomizer() {
        return factory -> {
            // 配置线程池
            factory.setThreadPool(new QueuedThreadPool(200, 8, 60000));

            // 配置连接器
            factory.addServerCustomizers(server -> {
                for (org.eclipse.jetty.server.Connector connector : server.getConnectors()) {
                    if (connector instanceof ServerConnector) {
                        ServerConnector serverConnector = (ServerConnector) connector;
                        serverConnector.setIdleTimeout(1200000);
                    }
                }
            });
        };
    }
}
