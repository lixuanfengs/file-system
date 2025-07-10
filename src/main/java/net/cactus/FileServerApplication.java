package net.cactus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileServerApplication {

    // 记录应用启动时间
    public static final long APPLICATION_START_TIME = System.currentTimeMillis();

    public static void main(String[] args) {
        SpringApplication.run(FileServerApplication.class, args);
    }
}
