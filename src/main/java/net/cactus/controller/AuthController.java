package net.cactus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 认证控制器 - 处理登录相关请求
 * 
 * @author FileServer
 * @version 1.0
 */
@Controller
public class AuthController {
    
    /**
     * 登录页面
     * 
     * @param error 错误信息
     * @param logout 登出信息
     * @param model 模型对象
     * @return 登录页面模板
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        
        if (error != null) {
            model.addAttribute("errorMessage", "用户名或密码错误，请重试！");
        }
        
        if (logout != null) {
            model.addAttribute("logoutMessage", "您已成功退出登录！");
        }
        
        return "auth/login";
    }
}
