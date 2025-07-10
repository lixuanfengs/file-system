package net.cactus.controller;

import net.cactus.pojo.Role;
import net.cactus.pojo.User;
import net.cactus.pojo.Permission;
import net.cactus.service.RoleService;
import net.cactus.service.UserService;
import net.cactus.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理控制器
 * 
 * @author FileServer
 * @version 1.0
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')")
public class UserManageController {

    private static final Logger logger = LoggerFactory.getLogger(UserManageController.class);

    @Value("${server.port:9101}")
    private String serverPort;

    @Value("${server.address:localhost}")
    private String serverAddress;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;
    
    /**
     * 用户管理页面
     */
    @GetMapping("/users")
    public String userManage(Model model) {
        addCommonAttributes(model);

        try {
            List<User> users = userService.findAllUsers();
            model.addAttribute("users", users != null ? users : new ArrayList<>());
        } catch (Exception e) {
            logger.error("Failed to load users: {}", e.getMessage());
            model.addAttribute("users", new ArrayList<>());
            model.addAttribute("error", "加载用户列表失败: " + e.getMessage());
        }

        return "admin/user-manage";
    }
    
    /**
     * 角色管理页面
     */
    @GetMapping("/roles")
    public String roleManage(Model model) {
        addCommonAttributes(model);

        try {
            List<Role> roles = roleService.findAllRoles();
            model.addAttribute("roles", roles != null ? roles : new ArrayList<>());
        } catch (Exception e) {
            logger.error("Failed to load roles: {}", e.getMessage());
            model.addAttribute("roles", new ArrayList<>());
            model.addAttribute("error", "加载角色列表失败: " + e.getMessage());
        }

        return "admin/role-manage";
    }

    /**
     * 系统统计页面
     */
    @GetMapping("/statistics")
    public String statistics(Model model) {
        addCommonAttributes(model);
        
        // 获取统计数据
        List<User> allUsers = userService.findAllUsers();
        List<Role> allRoles = roleService.findAllRoles();
        
        long totalUsers = allUsers.size();
        long activeUsers = allUsers.stream().filter(u -> u.getStatus() == 1).count();
        long totalRoles = allRoles.size();
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("totalRoles", totalRoles);
        model.addAttribute("recentUsers", allUsers.stream().limit(5).toList());
        
        return "admin/statistics";
    }

    /**
     * 权限管理页面
     */
    @GetMapping("/permissions")
    public String permissionManage(Model model) {
        addCommonAttributes(model);

        List<Permission> permissions = permissionService.findAllPermissions();
        List<Permission> permissionTree = permissionService.findPermissionTree();

        model.addAttribute("permissions", permissions);
        model.addAttribute("permissionTree", permissionTree);

        return "admin/permission-manage";
    }

    /**
     * 菜单管理页面
     */
    @GetMapping("/menus")
    public String menuManage(Model model) {
        addCommonAttributes(model);

        List<Permission> menuPermissions = permissionService.findMenuPermissions();
        List<Permission> menuTree = permissionService.findMenuPermissionTree();

        model.addAttribute("menuPermissions", menuPermissions);
        model.addAttribute("menuTree", menuTree);

        return "admin/menu-manage";
    }

    /**
     * 角色权限分配页面
     */
    @GetMapping("/roles/{id}/permissions")
    public String rolePermissions(@PathVariable Long id, Model model) {
        addCommonAttributes(model);

        Role role = roleService.findById(id);
        if (role == null) {
            return "redirect:/admin/roles?error=角色不存在";
        }

        List<Permission> allPermissions = permissionService.findPermissionTree();
        List<Permission> rolePermissions = permissionService.findPermissionsByRoleId(id);

        model.addAttribute("role", role);
        model.addAttribute("allPermissions", allPermissions);
        model.addAttribute("rolePermissions", rolePermissions);

        return "admin/role-permissions";
    }

    /**
     * 用户详情页面
     */
    @GetMapping("/users/{id}")
    public String userDetail(@PathVariable Long id, Model model) {
        addCommonAttributes(model);

        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/admin/users?error=用户不存在";
        }

        List<Role> userRoles = roleService.findRolesByUserId(id);
        List<Role> allRoles = roleService.findAllRoles();
        List<Permission> userPermissions = permissionService.findMenuPermissionTreeByUserId(id);

        model.addAttribute("user", user);
        model.addAttribute("userRoles", userRoles);
        model.addAttribute("allRoles", allRoles);
        model.addAttribute("userPermissions", userPermissions);

        return "admin/user-detail";
    }

    /**
     * 新增用户页面
     */
    @GetMapping("/users/new")
    public String newUser(Model model) {
        addCommonAttributes(model);

        List<Role> roles = roleService.findAllRoles();
        model.addAttribute("roles", roles);
        model.addAttribute("user", new User());

        return "admin/user-form";
    }

    /**
     * 编辑用户页面
     */
    @GetMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        addCommonAttributes(model);

        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/admin/users?error=用户不存在";
        }

        List<Role> roles = roleService.findAllRoles();
        List<Role> userRoles = roleService.findRolesByUserId(id);

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("userRoles", userRoles);

        return "admin/user-form";
    }

    /**
     * 添加通用属性
     */
    private void addCommonAttributes(Model model) {
        String baseUrl = "http://" + serverAddress + ":" + serverPort;
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("serverInfo", "FileServer v1.0 - Spring Boot 3.x + JDK 17 + Jetty");
        model.addAttribute("serverAddress", serverAddress);
        model.addAttribute("serverPort", serverPort);
    }
}
