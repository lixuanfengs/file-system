package net.cactus.service;

import net.cactus.pojo.Role;

import java.util.List;

/**
 * 角色服务接口
 * 
 * @author FileServer
 * @version 1.0
 */
public interface RoleService {
    
    /**
     * 根据ID查询角色
     * 
     * @param id 角色ID
     * @return 角色信息
     */
    Role findById(Long id);
    
    /**
     * 根据角色名查询角色
     * 
     * @param roleName 角色名
     * @return 角色信息
     */
    Role findByRoleName(String roleName);
    
    /**
     * 查询所有角色
     * 
     * @return 角色列表
     */
    List<Role> findAllRoles();
    
    /**
     * 创建新角色
     * 
     * @param role 角色信息
     * @return 创建的角色
     */
    Role createRole(Role role);
    
    /**
     * 更新角色信息
     * 
     * @param role 角色信息
     * @return 更新的角色
     */
    Role updateRole(Role role);
    
    /**
     * 删除角色
     * 
     * @param id 角色ID
     * @return 是否删除成功
     */
    boolean deleteRole(Long id);
    
    /**
     * 检查角色名是否存在
     * 
     * @param roleName 角色名
     * @return 是否存在
     */
    boolean existsByRoleName(String roleName);
    
    /**
     * 根据用户ID查询用户角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> findRolesByUserId(Long userId);
}
