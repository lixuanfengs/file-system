package net.cactus.dao;

import net.cactus.pojo.Role;
import net.cactus.pojo.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据访问层
 * 
 * @author FileServer
 * @version 1.0
 */
public interface UserDao {
    
    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM T_SYSTEM_USER WHERE username = #{username} AND status = 1")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "realName", column = "real_name"),
        @Result(property = "avatarUrl", column = "avatar_url"),
        @Result(property = "status", column = "status"),
        @Result(property = "lastLoginTime", column = "last_login_time"),
        @Result(property = "lastLoginIp", column = "last_login_ip"),
        @Result(property = "loginCount", column = "login_count"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "updateBy", column = "update_by"),
        @Result(property = "roles", column = "id", many = @Many(select = "findRolesByUserId"))
    })
    User findByUsername(@Param("username") String username);
    
    /**
     * 根据用户ID查询用户角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select({
        "SELECT r.* FROM T_SYSTEM_ROLE r ",
        "INNER JOIN T_SYSTEM_USER_ROLE ur ON r.id = ur.role_id ",
        "WHERE ur.user_id = #{userId} AND r.status = 1"
    })
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "roleName", column = "role_name"),
        @Result(property = "roleCode", column = "role_code"),
        @Result(property = "description", column = "description"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "updateBy", column = "update_by")
    })
    List<Role> findRolesByUserId(@Param("userId") Long userId);
    
    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @Select("SELECT * FROM T_SYSTEM_USER WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "realName", column = "real_name"),
        @Result(property = "avatarUrl", column = "avatar_url"),
        @Result(property = "status", column = "status"),
        @Result(property = "lastLoginTime", column = "last_login_time"),
        @Result(property = "lastLoginIp", column = "last_login_ip"),
        @Result(property = "loginCount", column = "login_count"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "updateBy", column = "update_by")
    })
    User findById(@Param("id") Long id);
    
    /**
     * 更新用户登录信息
     * 
     * @param userId 用户ID
     * @param loginTime 登录时间
     * @param loginIp 登录IP
     * @return 影响行数
     */
    @Update({
        "UPDATE T_SYSTEM_USER SET ",
        "last_login_time = #{loginTime}, ",
        "last_login_ip = #{loginIp}, ",
        "login_count = login_count + 1, ",
        "update_time = NOW() ",
        "WHERE id = #{userId}"
    })
    int updateLoginInfo(@Param("userId") Long userId, 
                       @Param("loginTime") LocalDateTime loginTime, 
                       @Param("loginIp") String loginIp);
    
    /**
     * 插入用户
     * 
     * @param user 用户信息
     * @return 影响行数
     */
    @Insert({
        "INSERT INTO T_SYSTEM_USER(",
        "username, password, email, phone, real_name, avatar_url, ",
        "status, create_time, create_by) VALUES(",
        "#{username}, #{password}, #{email}, #{phone}, #{realName}, #{avatarUrl}, ",
        "#{status}, #{createTime}, #{createBy})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 影响行数
     */
    @Update({
        "UPDATE T_SYSTEM_USER SET ",
        "email = #{email}, ",
        "phone = #{phone}, ",
        "real_name = #{realName}, ",
        "avatar_url = #{avatarUrl}, ",
        "status = #{status}, ",
        "update_time = NOW(), ",
        "update_by = #{updateBy} ",
        "WHERE id = #{id}"
    })
    int update(User user);
    
    /**
     * 查询所有用户
     * 
     * @return 用户列表
     */
    @Select("SELECT * FROM T_SYSTEM_USER ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "realName", column = "real_name"),
        @Result(property = "avatarUrl", column = "avatar_url"),
        @Result(property = "status", column = "status"),
        @Result(property = "lastLoginTime", column = "last_login_time"),
        @Result(property = "lastLoginIp", column = "last_login_ip"),
        @Result(property = "loginCount", column = "login_count"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<User> findAll();
    
    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    @Select("SELECT * FROM T_SYSTEM_USER WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM T_SYSTEM_USER WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 更新用户密码
     *
     * @param userId 用户ID
     * @param password 新密码
     * @param updateBy 更新人
     * @return 影响行数
     */
    @Update({
        "UPDATE T_SYSTEM_USER SET ",
        "password = #{password}, ",
        "update_time = NOW(), ",
        "update_by = #{updateBy} ",
        "WHERE id = #{userId}"
    })
    int updatePassword(@Param("userId") Long userId,
                      @Param("password") String password,
                      @Param("updateBy") String updateBy);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @param updateBy 更新人
     * @return 影响行数
     */
    @Update({
        "UPDATE T_SYSTEM_USER SET ",
        "status = #{status}, ",
        "update_time = NOW(), ",
        "update_by = #{updateBy} ",
        "WHERE id = #{userId}"
    })
    int updateStatus(@Param("userId") Long userId,
                    @Param("status") Integer status,
                    @Param("updateBy") String updateBy);
}
