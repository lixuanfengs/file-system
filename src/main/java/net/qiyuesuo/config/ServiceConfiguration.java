package net.qiyuesuo.config;

import net.qiyuesuo.service.FileMetaService;
import net.qiyuesuo.service.FileService;
import net.qiyuesuo.service.StorageService;
import net.qiyuesuo.service.impl.FileMetaServiceImpl;
import net.qiyuesuo.service.impl.FileServiceImpl;
import net.qiyuesuo.service.impl.StorageServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@ImportResource({"classpath:spring-mybatis.xml"})
@Configuration
/* loaded from: fileserver-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/net/qiyuesuo/config/ServiceConfiguration.class */
public class ServiceConfiguration {
    @Bean
    public FileMetaService fileMetaService() {
        return new FileMetaServiceImpl();
    }

    @Bean
    public StorageService storageService() {
        return new StorageServiceImpl();
    }

    @Bean
    public FileService fileService() {
        return new FileServiceImpl();
    }
}
