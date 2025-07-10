package net.cactus.pojo;

import net.cactus.annotation.TableColumn;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户实体类
 * 
 * @author FileServer
 * @version 1.0
 */
public class User implements UserDetails {

    @TableColumn(key = "id", label = "用户ID", order = 1, visible = false)
    private Long id;

    @TableColumn(key = "username", label = "用户名", order = 2)
    private String username;

    // 密码字段不在表格中显示
    private String password;

    @TableColumn(key = "email", label = "邮箱", order = 3)
    private String email;

    @TableColumn(key = "phone", label = "手机号", order = 4)
    private String phone;

    @TableColumn(key = "realName", label = "真实姓名", order = 5)
    private String realName;

    // 头像URL不在表格中显示
    private String avatarUrl;

    @TableColumn(key = "status", label = "状态", type = "badge", order = 6)
    private Integer status; // 0-禁用, 1-启用

    @TableColumn(key = "lastLoginTime", label = "最后登录", type = "date", order = 7)
    private LocalDateTime lastLoginTime;

    @TableColumn(key = "lastLoginIp", label = "登录IP", order = 8, visible = false)
    private String lastLoginIp;

    @TableColumn(key = "loginCount", label = "登录次数", order = 9)
    private Integer loginCount;

    @TableColumn(key = "createTime", label = "创建时间", type = "date", order = 10)
    private LocalDateTime createTime;

    // 更新时间、创建人、更新人不在表格中显示
    private LocalDateTime updateTime;
    private String createBy;
    private String updateBy;
    
    // 角色列表
    private List<Role> roles;
    
    // 构造函数
    public User() {}
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = 1;
        this.loginCount = 0;
    }
    
    // UserDetails 接口实现
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return List.of();
        }
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleCode()))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return status != null && status == 1;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return status != null && status == 1;
    }
    
    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }
    
    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    
    public String getLastLoginIp() {
        return lastLoginIp;
    }
    
    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }
    
    public Integer getLoginCount() {
        return loginCount;
    }
    
    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public String getCreateBy() {
        return createBy;
    }
    
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
    
    public String getUpdateBy() {
        return updateBy;
    }
    
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
    
    public List<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    
    // 便利方法
    public boolean hasRole(String roleCode) {
        if (roles == null) return false;
        return roles.stream().anyMatch(role -> roleCode.equals(role.getRoleCode()));
    }
    
    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN") || hasRole("ROLE_SUPER_ADMIN");
    }
    
    public String getDisplayName() {
        return realName != null && !realName.trim().isEmpty() ? realName : username;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", realName='" + realName + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
