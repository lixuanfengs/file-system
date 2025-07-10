package net.cactus.dao;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户角色关联数据访问层
 * 
 * @author FileServer
 * @version 1.0
 */
public interface UserRoleDao {
    
    /**
     * 为用户分配角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @param createBy 创建人
     * @return 影响行数
     */
    @Insert({
        "INSERT INTO T_SYSTEM_USER_ROLE(user_id, role_id, create_time, create_by) ",
        "VALUES(#{userId}, #{roleId}, NOW(), #{createBy})"
    })
    int assignRoleToUser(@Param("userId") Long userId, 
                        @Param("roleId") Long roleId, 
                        @Param("createBy") String createBy);
    
    /**
     * 移除用户角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Delete("DELETE FROM T_SYSTEM_USER_ROLE WHERE user_id = #{userId} AND role_id = #{roleId}")
    int removeRoleFromUser(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    /**
     * 清空用户的所有角色
     * 
     * @param userId 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM T_SYSTEM_USER_ROLE WHERE user_id = #{userId}")
    int clearUserRoles(@Param("userId") Long userId);
    
    /**
     * 查询用户的角色ID列表
     * 
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Select("SELECT role_id FROM T_SYSTEM_USER_ROLE WHERE user_id = #{userId}")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);
    
    /**
     * 查询角色的用户ID列表
     * 
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    @Select("SELECT user_id FROM T_SYSTEM_USER_ROLE WHERE role_id = #{roleId}")
    List<Long> findUserIdsByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 检查用户是否拥有指定角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否拥有角色
     */
    @Select("SELECT COUNT(*) FROM T_SYSTEM_USER_ROLE WHERE user_id = #{userId} AND role_id = #{roleId}")
    int checkUserHasRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
    
    /**
     * 批量为用户分配角色
     * 
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @param createBy 创建人
     * @return 影响行数
     */
    @Insert({
        "<script>",
        "INSERT INTO T_SYSTEM_USER_ROLE(user_id, role_id, create_time, create_by) VALUES ",
        "<foreach collection='roleIds' item='roleId' separator=','>",
        "(#{userId}, #{roleId}, NOW(), #{createBy})",
        "</foreach>",
        "</script>"
    })
    int batchAssignRolesToUser(@Param("userId") Long userId, 
                              @Param("roleIds") List<Long> roleIds, 
                              @Param("createBy") String createBy);
}
