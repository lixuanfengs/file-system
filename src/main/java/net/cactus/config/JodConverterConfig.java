package net.cactus.config;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.ExternalOfficeManager;  // 注意这里
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "jodconverter.remote.enabled", havingValue = "true", matchIfMissing = true)
public class JodConverterConfig {

    private static final Logger logger = LoggerFactory.getLogger(JodConverterConfig.class);

    /**
     * 通过 ExternalOfficeManager 远程连接到已经启动的 LibreOffice 服务，
     * 不再 fork/启动新的 soffice 进程。
     */
    @Bean(destroyMethod = "stop")
    public OfficeManager officeManager() {
        OfficeManager manager = ExternalOfficeManager.builder()
                .hostName("127.0.0.1")
                .portNumbers(2002)        // UNO 接口端口
                .build();

        try {
            logger.info("Connecting to remote LibreOffice on 127.0.0.1:2002...");
            manager.start();
            logger.info("Connected to LibreOffice service");
        } catch (Exception e) {
            logger.error("Failed to connect to LibreOffice service", e);
        }
        return manager;
    }

    @Bean
    public DocumentConverter documentConverter(OfficeManager officeManager) {
        return LocalConverter.builder()
                .officeManager(officeManager)
                .build();
    }
}
