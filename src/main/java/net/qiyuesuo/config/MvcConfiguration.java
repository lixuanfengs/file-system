package net.qiyuesuo.config;

import java.util.List;
import javax.servlet.annotation.MultipartConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@MultipartConfig
@Configuration
/* loaded from: fileserver-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/net/qiyuesuo/config/MvcConfiguration.class */
public class MvcConfiguration implements WebMvcConfigurer {
    @Override // org.springframework.web.servlet.config.annotation.WebMvcConfigurer
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
    }
}
