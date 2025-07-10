package net.cactus.service.impl;

import net.cactus.dao.RoleDao;
import net.cactus.pojo.Role;
import net.cactus.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色服务实现类
 * 
 * @author FileServer
 * @version 1.0
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    
    @Autowired
    private RoleDao roleDao;
    
    @Override
    public Role findById(Long id) {
        return roleDao.findById(id);
    }
    
    @Override
    public Role findByRoleName(String roleName) {
        return roleDao.findByRoleName(roleName);
    }
    
    @Override
    public List<Role> findAllRoles() {
        return roleDao.findAll();
    }
    
    @Override
    public Role createRole(Role role) {
        // 设置创建时间
        role.setCreateTime(LocalDateTime.now());
        
        // 设置默认状态
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        
        int result = roleDao.insert(role);
        if (result > 0) {
            logger.info("Role created successfully: {}", role.getRoleName());
            return role;
        } else {
            logger.error("Failed to create role: {}", role.getRoleName());
            return null;
        }
    }
    
    @Override
    public Role updateRole(Role role) {
        role.setUpdateTime(LocalDateTime.now());
        int result = roleDao.update(role);
        if (result > 0) {
            logger.info("Role updated successfully: {}", role.getRoleName());
            return role;
        } else {
            logger.error("Failed to update role: {}", role.getRoleName());
            return null;
        }
    }
    
    @Override
    public boolean deleteRole(Long id) {
        try {
            int result = roleDao.deleteById(id);
            if (result > 0) {
                logger.info("Role deleted successfully: {}", id);
                return true;
            } else {
                logger.warn("Role not found for deletion: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to delete role {}: {}", id, e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean existsByRoleName(String roleName) {
        Role role = roleDao.findByRoleName(roleName);
        return role != null;
    }
    
    @Override
    public List<Role> findRolesByUserId(Long userId) {
        return roleDao.findRolesByUserId(userId);
    }
}
