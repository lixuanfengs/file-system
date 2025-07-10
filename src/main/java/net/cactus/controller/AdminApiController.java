package net.cactus.controller;

import net.cactus.pojo.User;
import net.cactus.pojo.Role;
import net.cactus.pojo.Permission;
import net.cactus.service.UserService;
import net.cactus.service.RoleService;
import net.cactus.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理API控制器
 * 
 * @author FileServer
 * @version 1.0
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_SUPER_ADMIN') or hasRole('ROLE_ADMIN')")
public class AdminApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminApiController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private PermissionService permissionService;
    
    // ==================== 用户管理API ====================
    
    /**
     * 创建用户
     */
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user, Authentication auth) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查用户名是否已存在
            if (userService.existsByUsername(user.getUsername())) {
                result.put("success", false);
                result.put("message", "用户名已存在");
                return ResponseEntity.badRequest().body(result);
            }
            
            // 检查邮箱是否已存在
            if (user.getEmail() != null && userService.existsByEmail(user.getEmail())) {
                result.put("success", false);
                result.put("message", "邮箱已存在");
                return ResponseEntity.badRequest().body(result);
            }
            
            user.setCreateBy(auth.getName());
            User createdUser = userService.createUser(user);
            
            if (createdUser != null) {
                result.put("success", true);
                result.put("message", "用户创建成功");
                result.put("data", createdUser);
            } else {
                result.put("success", false);
                result.put("message", "用户创建失败");
            }
            
        } catch (Exception e) {
            logger.error("Failed to create user: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, 
                                                         @RequestBody User user, 
                                                         Authentication auth) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            User existingUser = userService.findById(id);
            if (existingUser == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
            
            // 更新用户信息
            user.setId(id);
            user.setUpdateBy(auth.getName());
            User updatedUser = userService.updateUser(user);
            
            if (updatedUser != null) {
                result.put("success", true);
                result.put("message", "用户更新成功");
                result.put("data", updatedUser);
            } else {
                result.put("success", false);
                result.put("message", "用户更新失败");
            }
            
        } catch (Exception e) {
            logger.error("Failed to update user: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id, Authentication auth) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            User user = userService.findById(id);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
            
            // 不能删除自己
            if (user.getUsername().equals(auth.getName())) {
                result.put("success", false);
                result.put("message", "不能删除自己");
                return ResponseEntity.badRequest().body(result);
            }
            
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                result.put("success", true);
                result.put("message", "用户删除成功");
            } else {
                result.put("success", false);
                result.put("message", "用户删除失败");
            }
            
        } catch (Exception e) {
            logger.error("Failed to delete user: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 为用户分配角色
     */
    @PostMapping("/users/{id}/roles")
    public ResponseEntity<Map<String, Object>> assignRolesToUser(@PathVariable Long id, 
                                                               @RequestBody List<Long> roleIds, 
                                                               Authentication auth) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            User user = userService.findById(id);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
            
            boolean assigned = userService.assignRolesToUser(id, roleIds, auth.getName());
            if (assigned) {
                result.put("success", true);
                result.put("message", "角色分配成功");
            } else {
                result.put("success", false);
                result.put("message", "角色分配失败");
            }
            
        } catch (Exception e) {
            logger.error("Failed to assign roles to user: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新用户状态
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(@PathVariable Long id, 
                                                               @RequestParam Integer status, 
                                                               Authentication auth) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            User user = userService.findById(id);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
            
            // 不能禁用自己
            if (user.getUsername().equals(auth.getName()) && status == 0) {
                result.put("success", false);
                result.put("message", "不能禁用自己");
                return ResponseEntity.badRequest().body(result);
            }
            
            boolean updated = userService.updateUserStatus(id, status, auth.getName());
            if (updated) {
                result.put("success", true);
                result.put("message", "状态更新成功");
            } else {
                result.put("success", false);
                result.put("message", "状态更新失败");
            }
            
        } catch (Exception e) {
            logger.error("Failed to update user status: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 重置用户密码
     */
    @PutMapping("/users/{id}/password")
    public ResponseEntity<Map<String, Object>> resetUserPassword(@PathVariable Long id, 
                                                                @RequestParam String newPassword, 
                                                                Authentication auth) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            User user = userService.findById(id);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
            
            boolean updated = userService.updateUserPassword(id, newPassword, auth.getName());
            if (updated) {
                result.put("success", true);
                result.put("message", "密码重置成功");
            } else {
                result.put("success", false);
                result.put("message", "密码重置失败");
            }
            
        } catch (Exception e) {
            logger.error("Failed to reset user password: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
        
        return ResponseEntity.ok(result);
    }

    // ==================== 角色管理API ====================

    /**
     * 创建角色
     */
    @PostMapping("/roles")
    public ResponseEntity<Map<String, Object>> createRole(@RequestBody Role role, Authentication auth) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查角色名是否已存在
            if (roleService.existsByRoleName(role.getRoleName())) {
                result.put("success", false);
                result.put("message", "角色名已存在");
                return ResponseEntity.badRequest().body(result);
            }

            role.setCreateBy(auth.getName());
            Role createdRole = roleService.createRole(role);

            if (createdRole != null) {
                result.put("success", true);
                result.put("message", "角色创建成功");
                result.put("data", createdRole);
            } else {
                result.put("success", false);
                result.put("message", "角色创建失败");
            }

        } catch (Exception e) {
            logger.error("Failed to create role: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 更新角色
     */
    @PutMapping("/roles/{id}")
    public ResponseEntity<Map<String, Object>> updateRole(@PathVariable Long id,
                                                         @RequestBody Role role,
                                                         Authentication auth) {
        Map<String, Object> result = new HashMap<>();

        try {
            Role existingRole = roleService.findById(id);
            if (existingRole == null) {
                result.put("success", false);
                result.put("message", "角色不存在");
                return ResponseEntity.notFound().build();
            }

            // 更新角色信息
            role.setId(id);
            role.setUpdateBy(auth.getName());
            Role updatedRole = roleService.updateRole(role);

            if (updatedRole != null) {
                result.put("success", true);
                result.put("message", "角色更新成功");
                result.put("data", updatedRole);
            } else {
                result.put("success", false);
                result.put("message", "角色更新失败");
            }

        } catch (Exception e) {
            logger.error("Failed to update role: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Map<String, Object>> deleteRole(@PathVariable Long id, Authentication auth) {
        Map<String, Object> result = new HashMap<>();

        try {
            Role role = roleService.findById(id);
            if (role == null) {
                result.put("success", false);
                result.put("message", "角色不存在");
                return ResponseEntity.notFound().build();
            }

            // 不能删除系统内置角色
            if ("ROLE_SUPER_ADMIN".equals(role.getRoleCode()) || "ROLE_ADMIN".equals(role.getRoleCode())) {
                result.put("success", false);
                result.put("message", "不能删除系统内置角色");
                return ResponseEntity.badRequest().body(result);
            }

            boolean deleted = roleService.deleteRole(id);
            if (deleted) {
                result.put("success", true);
                result.put("message", "角色删除成功");
            } else {
                result.put("success", false);
                result.put("message", "角色删除失败");
            }

        } catch (Exception e) {
            logger.error("Failed to delete role: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/roles/{id}/permissions")
    public ResponseEntity<Map<String, Object>> assignPermissionsToRole(@PathVariable Long id,
                                                                      @RequestBody List<Long> permissionIds,
                                                                      Authentication auth) {
        Map<String, Object> result = new HashMap<>();

        try {
            Role role = roleService.findById(id);
            if (role == null) {
                result.put("success", false);
                result.put("message", "角色不存在");
                return ResponseEntity.notFound().build();
            }

            boolean assigned = permissionService.assignPermissionsToRole(id, permissionIds, auth.getName());
            if (assigned) {
                result.put("success", true);
                result.put("message", "权限分配成功");
            } else {
                result.put("success", false);
                result.put("message", "权限分配失败");
            }

        } catch (Exception e) {
            logger.error("Failed to assign permissions to role: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }

    // ==================== 权限管理API ====================

    /**
     * 获取权限树
     */
    @GetMapping("/permissions/tree")
    public ResponseEntity<Map<String, Object>> getPermissionTree() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Permission> permissionTree = permissionService.findPermissionTree();
            result.put("success", true);
            result.put("data", permissionTree);

        } catch (Exception e) {
            logger.error("Failed to get permission tree: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 获取菜单权限树
     */
    @GetMapping("/permissions/menu-tree")
    public ResponseEntity<Map<String, Object>> getMenuPermissionTree() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Permission> menuTree = permissionService.findMenuPermissionTree();
            result.put("success", true);
            result.put("data", menuTree);

        } catch (Exception e) {
            logger.error("Failed to get menu permission tree: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 创建权限
     */
    @PostMapping("/permissions")
    public ResponseEntity<Map<String, Object>> createPermission(@RequestBody Permission permission, Authentication auth) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查权限编码是否已存在
            if (permissionService.existsByPermissionCode(permission.getPermissionCode())) {
                result.put("success", false);
                result.put("message", "权限编码已存在");
                return ResponseEntity.badRequest().body(result);
            }

            permission.setCreateBy(auth.getName());
            Permission createdPermission = permissionService.createPermission(permission);

            if (createdPermission != null) {
                result.put("success", true);
                result.put("message", "权限创建成功");
                result.put("data", createdPermission);
            } else {
                result.put("success", false);
                result.put("message", "权限创建失败");
            }

        } catch (Exception e) {
            logger.error("Failed to create permission: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 更新权限
     */
    @PutMapping("/permissions/{id}")
    public ResponseEntity<Map<String, Object>> updatePermission(@PathVariable Long id,
                                                               @RequestBody Permission permission,
                                                               Authentication auth) {
        Map<String, Object> result = new HashMap<>();

        try {
            Permission existingPermission = permissionService.findById(id);
            if (existingPermission == null) {
                result.put("success", false);
                result.put("message", "权限不存在");
                return ResponseEntity.notFound().build();
            }

            // 更新权限信息
            permission.setId(id);
            permission.setUpdateBy(auth.getName());
            Permission updatedPermission = permissionService.updatePermission(permission);

            if (updatedPermission != null) {
                result.put("success", true);
                result.put("message", "权限更新成功");
                result.put("data", updatedPermission);
            } else {
                result.put("success", false);
                result.put("message", "权限更新失败");
            }

        } catch (Exception e) {
            logger.error("Failed to update permission: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Map<String, Object>> deletePermission(@PathVariable Long id, Authentication auth) {
        Map<String, Object> result = new HashMap<>();

        try {
            Permission permission = permissionService.findById(id);
            if (permission == null) {
                result.put("success", false);
                result.put("message", "权限不存在");
                return ResponseEntity.notFound().build();
            }

            boolean deleted = permissionService.deletePermission(id);
            if (deleted) {
                result.put("success", true);
                result.put("message", "权限删除成功");
            } else {
                result.put("success", false);
                result.put("message", "权限删除失败，可能存在子权限");
            }

        } catch (Exception e) {
            logger.error("Failed to delete permission: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 获取角色的权限列表
     */
    @GetMapping("/roles/{id}/permissions")
    public ResponseEntity<Map<String, Object>> getRolePermissions(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        try {
            Role role = roleService.findById(id);
            if (role == null) {
                result.put("success", false);
                result.put("message", "角色不存在");
                return ResponseEntity.notFound().build();
            }

            List<Permission> permissions = permissionService.findPermissionsByRoleId(id);
            result.put("success", true);
            result.put("data", permissions);

        } catch (Exception e) {
            logger.error("Failed to get role permissions: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 获取用户的菜单权限树
     */
    @GetMapping("/users/{id}/menu-permissions")
    public ResponseEntity<Map<String, Object>> getUserMenuPermissions(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        try {
            User user = userService.findById(id);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }

            List<Permission> menuPermissions = permissionService.findMenuPermissionTreeByUserId(id);
            result.put("success", true);
            result.put("data", menuPermissions);

        } catch (Exception e) {
            logger.error("Failed to get user menu permissions: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }
}
