package net.cactus.service.impl;

import net.cactus.dao.UserDao;
import net.cactus.pojo.User;
import net.cactus.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 * 
 * @author FileServer
 * @version 1.0
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserDao userDao;
    
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        
        User user = userDao.findByUsername(username);
        if (user == null) {
            logger.warn("User not found: {}", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        
        logger.debug("User loaded successfully: {}", user.getUsername());
        return user;
    }
    
    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }
    
    @Override
    public User findById(Long id) {
        return userDao.findById(id);
    }
    
    @Override
    public boolean updateLoginInfo(Long userId, String loginIp) {
        try {
            LocalDateTime loginTime = LocalDateTime.now();
            int result = userDao.updateLoginInfo(userId, loginTime, loginIp);
            logger.info("Updated login info for user {}: IP={}, Time={}", userId, loginIp, loginTime);
            return result > 0;
        } catch (Exception e) {
            logger.error("Failed to update login info for user {}: {}", userId, e.getMessage());
            return false;
        }
    }
    
    @Override
    public User createUser(User user) {
        // 加密密码
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // 设置创建时间
        user.setCreateTime(LocalDateTime.now());
        
        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        
        // 设置默认登录次数
        if (user.getLoginCount() == null) {
            user.setLoginCount(0);
        }
        
        int result = userDao.insert(user);
        if (result > 0) {
            logger.info("User created successfully: {}", user.getUsername());
            return user;
        } else {
            logger.error("Failed to create user: {}", user.getUsername());
            return null;
        }
    }
    
    @Override
    public User updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        int result = userDao.update(user);
        if (result > 0) {
            logger.info("User updated successfully: {}", user.getUsername());
            return user;
        } else {
            logger.error("Failed to update user: {}", user.getUsername());
            return null;
        }
    }
    
    @Override
    public List<User> findAllUsers() {
        return userDao.findAll();
    }
    
    @Override
    public boolean existsByUsername(String username) {
        User user = userDao.findByUsername(username);
        return user != null;
    }
    
    @Override
    public boolean existsByEmail(String email) {
        User user = userDao.findByEmail(email);
        return user != null;
    }
}
