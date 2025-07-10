package net.cactus.service.impl;

import net.cactus.dao.PermissionDao;
import net.cactus.dao.RolePermissionDao;
import net.cactus.pojo.Permission;
import net.cactus.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 * 
 * @author FileServer
 * @version 1.0
 */
@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {
    
    private static final Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);
    
    @Autowired
    private PermissionDao permissionDao;
    
    @Autowired
    private RolePermissionDao rolePermissionDao;
    
    @Override
    public Permission findById(Long id) {
        return permissionDao.findById(id);
    }
    
    @Override
    public Permission findByPermissionCode(String permissionCode) {
        return permissionDao.findByPermissionCode(permissionCode);
    }
    
    @Override
    public List<Permission> findAllPermissions() {
        return permissionDao.findAll();
    }
    
    @Override
    public List<Permission> findPermissionTree() {
        List<Permission> allPermissions = permissionDao.findAll();
        return buildPermissionTree(allPermissions);
    }
    
    @Override
    public List<Permission> findByParentId(Long parentId) {
        return permissionDao.findByParentId(parentId);
    }
    
    @Override
    public List<Permission> findByPermissionType(Integer permissionType) {
        return permissionDao.findByPermissionType(permissionType);
    }
    
    @Override
    public List<Permission> findMenuPermissions() {
        return permissionDao.findByPermissionType(1); // 1-菜单
    }
    
    @Override
    public List<Permission> findMenuPermissionTree() {
        List<Permission> menuPermissions = findMenuPermissions();
        return buildPermissionTree(menuPermissions);
    }
    
    @Override
    public Permission createPermission(Permission permission) {
        // 设置创建时间
        permission.setCreateTime(LocalDateTime.now());
        
        // 设置默认状态
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }
        
        // 设置默认排序
        if (permission.getSortOrder() == null) {
            permission.setSortOrder(0);
        }
        
        // 设置默认父ID
        if (permission.getParentId() == null) {
            permission.setParentId(0L);
        }
        
        int result = permissionDao.insert(permission);
        if (result > 0) {
            logger.info("Permission created successfully: {}", permission.getPermissionName());
            return permission;
        } else {
            logger.error("Failed to create permission: {}", permission.getPermissionName());
            return null;
        }
    }
    
    @Override
    public Permission updatePermission(Permission permission) {
        permission.setUpdateTime(LocalDateTime.now());
        int result = permissionDao.update(permission);
        if (result > 0) {
            logger.info("Permission updated successfully: {}", permission.getPermissionName());
            return permission;
        } else {
            logger.error("Failed to update permission: {}", permission.getPermissionName());
            return null;
        }
    }
    
    @Override
    public boolean deletePermission(Long id) {
        try {
            // 检查是否有子权限
            List<Permission> children = permissionDao.findByParentId(id);
            if (!children.isEmpty()) {
                logger.warn("Cannot delete permission {} because it has children", id);
                return false;
            }
            
            int result = permissionDao.deleteById(id);
            if (result > 0) {
                logger.info("Permission deleted successfully: {}", id);
                return true;
            } else {
                logger.warn("Permission not found for deletion: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to delete permission {}: {}", id, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean existsByPermissionCode(String permissionCode) {
        Permission permission = permissionDao.findByPermissionCode(permissionCode);
        return permission != null;
    }
    
    @Override
    public List<Permission> findPermissionsByRoleId(Long roleId) {
        return permissionDao.findPermissionsByRoleId(roleId);
    }
    
    @Override
    public List<Permission> findPermissionsByUserId(Long userId) {
        return permissionDao.findPermissionsByUserId(userId);
    }
    
    @Override
    public List<Permission> findMenuPermissionsByUserId(Long userId) {
        List<Permission> userPermissions = permissionDao.findPermissionsByUserId(userId);
        return userPermissions.stream()
                .filter(Permission::isMenu)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Permission> findMenuPermissionTreeByUserId(Long userId) {
        List<Permission> menuPermissions = findMenuPermissionsByUserId(userId);
        return buildPermissionTree(menuPermissions);
    }
    
    @Override
    public boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds, String operator) {
        try {
            // 先清空角色的所有权限
            rolePermissionDao.clearRolePermissions(roleId);
            
            // 批量分配新权限
            if (permissionIds != null && !permissionIds.isEmpty()) {
                int result = rolePermissionDao.batchAssignPermissionsToRole(roleId, permissionIds, operator);
                logger.info("Assigned {} permissions to role {}", result, roleId);
            }
            
            return true;
        } catch (Exception e) {
            logger.error("Failed to assign permissions to role {}: {}", roleId, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean removePermissionFromRole(Long roleId, Long permissionId) {
        try {
            int result = rolePermissionDao.removePermissionFromRole(roleId, permissionId);
            if (result > 0) {
                logger.info("Removed permission {} from role {}", permissionId, roleId);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Failed to remove permission {} from role {}: {}", permissionId, roleId, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean clearRolePermissions(Long roleId) {
        try {
            int result = rolePermissionDao.clearRolePermissions(roleId);
            logger.info("Cleared {} permissions from role {}", result, roleId);
            return true;
        } catch (Exception e) {
            logger.error("Failed to clear permissions from role {}: {}", roleId, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean checkRoleHasPermission(Long roleId, String permissionCode) {
        Permission permission = permissionDao.findByPermissionCode(permissionCode);
        if (permission == null) {
            return false;
        }
        
        int count = rolePermissionDao.checkRoleHasPermission(roleId, permission.getId());
        return count > 0;
    }
    
    @Override
    public boolean checkUserHasPermission(Long userId, String permissionCode) {
        List<Permission> userPermissions = permissionDao.findPermissionsByUserId(userId);
        return userPermissions.stream()
                .anyMatch(p -> permissionCode.equals(p.getPermissionCode()));
    }
    
    /**
     * 构建权限树
     * 
     * @param permissions 权限列表
     * @return 权限树
     */
    private List<Permission> buildPermissionTree(List<Permission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 按父ID分组
        Map<Long, List<Permission>> permissionMap = permissions.stream()
                .collect(Collectors.groupingBy(Permission::getParentId));
        
        // 构建树形结构
        List<Permission> rootPermissions = permissionMap.getOrDefault(0L, new ArrayList<>());
        buildChildren(rootPermissions, permissionMap);
        
        return rootPermissions;
    }
    
    /**
     * 递归构建子权限
     * 
     * @param permissions 当前层级权限
     * @param permissionMap 权限映射
     */
    private void buildChildren(List<Permission> permissions, Map<Long, List<Permission>> permissionMap) {
        for (Permission permission : permissions) {
            List<Permission> children = permissionMap.getOrDefault(permission.getId(), new ArrayList<>());
            permission.setChildren(children);
            if (!children.isEmpty()) {
                buildChildren(children, permissionMap);
            }
        }
    }
}
