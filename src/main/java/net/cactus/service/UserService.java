package net.cactus.service;

import net.cactus.pojo.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * 用户服务接口
 * 
 * @author FileServer
 * @version 1.0
 */
public interface UserService extends UserDetailsService {
    
    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);
    
    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    User findById(Long id);
    
    /**
     * 更新用户登录信息
     * 
     * @param userId 用户ID
     * @param loginIp 登录IP
     * @return 是否更新成功
     */
    boolean updateLoginInfo(Long userId, String loginIp);
    
    /**
     * 创建新用户
     * 
     * @param user 用户信息
     * @return 创建的用户
     */
    User createUser(User user);
    
    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 更新的用户
     */
    User updateUser(User user);
    
    /**
     * 查询所有用户
     * 
     * @return 用户列表
     */
    List<User> findAllUsers();
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long id);

    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @param operator 操作人
     * @return 是否分配成功
     */
    boolean assignRolesToUser(Long userId, List<Long> roleIds, String operator);

    /**
     * 移除用户角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否移除成功
     */
    boolean removeRoleFromUser(Long userId, Long roleId);

    /**
     * 清空用户的所有角色
     *
     * @param userId 用户ID
     * @return 是否清空成功
     */
    boolean clearUserRoles(Long userId);

    /**
     * 检查用户是否拥有指定角色
     *
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否拥有角色
     */
    boolean checkUserHasRole(Long userId, String roleCode);

    /**
     * 更新用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @param operator 操作人
     * @return 是否更新成功
     */
    boolean updateUserPassword(Long userId, String newPassword, String operator);

    /**
     * 启用/禁用用户
     *
     * @param userId 用户ID
     * @param status 状态 0-禁用, 1-启用
     * @param operator 操作人
     * @return 是否更新成功
     */
    boolean updateUserStatus(Long userId, Integer status, String operator);
}
