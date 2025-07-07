package net.qiyuesuo.config;

import net.qiyuesuo.utils.file.PropertiesUtils;
import org.springframework.stereotype.Component;

@Component
public class Config {

    public String FILESTORE;
    public String SERVERHOST;
    public String SERVERPORT;

    public Config() {
        try {
            this.FILESTORE = PropertiesUtils.getConfigProperty("filestore");
            this.SERVERHOST = PropertiesUtils.getConfigProperty("serverhost");
            this.SERVERPORT = PropertiesUtils.getConfigProperty("serverport");
        } catch (Exception e) {
            // 设置默认值
            this.FILESTORE = "/tmp/filestore";
            this.SERVERHOST = "localhost";
            this.SERVERPORT = "9100";
        }
    }
}
