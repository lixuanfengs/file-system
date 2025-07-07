package net.cactus.config;

import net.cactus.service.FileMetaService;
import net.cactus.service.FileService;
import net.cactus.service.StorageService;
import net.cactus.service.impl.FileMetaServiceImpl;
import net.cactus.service.impl.FileServiceImpl;
import net.cactus.service.impl.StorageServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@ImportResource({"classpath:spring-mybatis.xml"})
@Configuration
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
