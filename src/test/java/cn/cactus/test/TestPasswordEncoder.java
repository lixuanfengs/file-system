package cn.cactus.test;

import net.cactus.FileServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Package: cn.cactus.test
 * Description:
 *
 * @Author 仙人球⁶ᴳ |
 * @Date 2025/7/8 10:41
 * @Github https://github.com/lixuanfengs
 */

@SpringBootTest(classes = FileServerApplication.class)
public class TestPasswordEncoder {

    @Test
    public void passwordEncoder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}
