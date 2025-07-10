package net.cactus.service;

import net.cactus.pojo.Permission;

import java.util.List;

/**
 * 权限服务接口
 * 
 * @author FileServer
 * @version 1.0
 */
public interface PermissionService {
    
    /**
     * 根据ID查询权限
     * 
     * @param id 权限ID
     * @return 权限信息
     */
    Permission findById(Long id);
    
    /**
     * 根据权限编码查询权限
     * 
     * @param permissionCode 权限编码
     * @return 权限信息
     */
    Permission findByPermissionCode(String permissionCode);
    
    /**
     * 查询所有权限
     * 
     * @return 权限列表
     */
    List<Permission> findAllPermissions();
    
    /**
     * 查询权限树（包含父子关系）
     * 
     * @return 权限树列表
     */
    List<Permission> findPermissionTree();
    
    /**
     * 根据父权限ID查询子权限
     * 
     * @param parentId 父权限ID
     * @return 权限列表
     */
    List<Permission> findByParentId(Long parentId);
    
    /**
     * 根据权限类型查询权限
     * 
     * @param permissionType 权限类型
     * @return 权限列表
     */
    List<Permission> findByPermissionType(Integer permissionType);
    
    /**
     * 查询菜单权限
     * 
     * @return 菜单权限列表
     */
    List<Permission> findMenuPermissions();
    
    /**
     * 查询菜单权限树
     * 
     * @return 菜单权限树列表
     */
    List<Permission> findMenuPermissionTree();
    
    /**
     * 创建新权限
     * 
     * @param permission 权限信息
     * @return 创建的权限
     */
    Permission createPermission(Permission permission);
    
    /**
     * 更新权限信息
     * 
     * @param permission 权限信息
     * @return 更新的权限
     */
    Permission updatePermission(Permission permission);
    
    /**
     * 删除权限
     * 
     * @param id 权限ID
     * @return 是否删除成功
     */
    boolean deletePermission(Long id);
    
    /**
     * 检查权限编码是否存在
     * 
     * @param permissionCode 权限编码
     * @return 是否存在
     */
    boolean existsByPermissionCode(String permissionCode);
    
    /**
     * 根据角色ID查询角色权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> findPermissionsByRoleId(Long roleId);
    
    /**
     * 根据用户ID查询用户权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> findPermissionsByUserId(Long userId);
    
    /**
     * 根据用户ID查询用户菜单权限
     * 
     * @param userId 用户ID
     * @return 菜单权限列表
     */
    List<Permission> findMenuPermissionsByUserId(Long userId);
    
    /**
     * 根据用户ID查询用户菜单权限树
     * 
     * @param userId 用户ID
     * @return 菜单权限树列表
     */
    List<Permission> findMenuPermissionTreeByUserId(Long userId);
    
    /**
     * 为角色分配权限
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @param operator 操作人
     * @return 是否分配成功
     */
    boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds, String operator);
    
    /**
     * 移除角色权限
     * 
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 是否移除成功
     */
    boolean removePermissionFromRole(Long roleId, Long permissionId);
    
    /**
     * 清空角色的所有权限
     * 
     * @param roleId 角色ID
     * @return 是否清空成功
     */
    boolean clearRolePermissions(Long roleId);
    
    /**
     * 检查角色是否拥有指定权限
     * 
     * @param roleId 角色ID
     * @param permissionCode 权限编码
     * @return 是否拥有权限
     */
    boolean checkRoleHasPermission(Long roleId, String permissionCode);
    
    /**
     * 检查用户是否拥有指定权限
     * 
     * @param userId 用户ID
     * @param permissionCode 权限编码
     * @return 是否拥有权限
     */
    boolean checkUserHasPermission(Long userId, String permissionCode);
}
