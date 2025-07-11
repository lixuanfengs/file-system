package net.cactus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {

    @Value("${fileserver.filestore:/tmp/filestore}")
    public String FILESTORE;

    @Value("${fileserver.serverhost:localhost}")
    public String SERVERHOST;

    @Value("${fileserver.serverport:9100}")
    public String SERVERPORT;
}
