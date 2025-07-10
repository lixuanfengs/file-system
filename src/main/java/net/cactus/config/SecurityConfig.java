package net.cactus.config;

import net.cactus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security 配置类
 * 
 * @author FileServer
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private UserService userService;
    
    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    

    
    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * 登录成功处理器
     */
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            // 更新用户登录信息
            String username = authentication.getName();
            String clientIp = getClientIpAddress(request);
            
            // 异步更新登录信息
            try {
                net.cactus.pojo.User user = userService.findByUsername(username);
                if (user != null) {
                    userService.updateLoginInfo(user.getId(), clientIp);
                }
            } catch (Exception e) {
                // 记录日志但不影响登录流程
                System.err.println("Failed to update login info: " + e.getMessage());
            }
            
            // 重定向到首页
            response.sendRedirect("/page/");
        };
    }
    
    /**
     * 登录失败处理器
     */
    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            // 重定向到登录页面并显示错误信息
            response.sendRedirect("/login?error=true");
        };
    }
    
    /**
     * 安全过滤器链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 配置用户详情服务和密码编码器
            .userDetailsService(userService)
            // 禁用 CSRF（对于文件上传API和登录）
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/file/**", "/api/**", "/login")
            )
            
            // 配置授权规则
            .authorizeHttpRequests(authz -> authz
                // 公开访问的资源
                .requestMatchers(
                    "/login", "/login/**",
                    "/css/**", "/js/**", "/images/**", "/static/**",
                    "/health", "/error",
                    "/favicon.ico",
                    "/api/system/info",
                    "/api/system/monitor",
                    "/api/system/overview"
                ).permitAll()
                
                // 文件操作需要认证
                .requestMatchers("/file/**").authenticated()
                
                // 页面访问需要认证
                .requestMatchers("/page/**").authenticated()
                
                // 管理功能需要管理员权限
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            
            // 配置登录
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler())
                .failureHandler(failureHandler())
                .permitAll()
            )
            
            // 配置登出
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            
            // 配置会话管理
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            
            // 配置记住我功能
            .rememberMe(remember -> remember
                .key("fileserver-remember-me")
                .tokenValiditySeconds(7 * 24 * 60 * 60) // 7天
                .userDetailsService(userService)
            );
        
        return http.build();
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(jakarta.servlet.http.HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        String xForwardedProto = request.getHeader("X-Forwarded-Proto");
        if (xForwardedProto != null && !xForwardedProto.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedProto)) {
            return xForwardedProto;
        }

        return request.getRemoteAddr();
    }
}
