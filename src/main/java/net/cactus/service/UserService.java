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
}
