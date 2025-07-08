-- FileServer 用户管理系统 DDL 语句
-- 创建时间: 2025-07-07
-- 描述: 用户表、角色表、用户角色关联表

-- 1. 用户表
CREATE TABLE T_SYSTEM_USER (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    real_name VARCHAR(50) COMMENT '真实姓名',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    login_count INT DEFAULT 0 COMMENT '登录次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人'
) COMMENT='系统用户表';

-- 2. 角色表
CREATE TABLE T_SYSTEM_ROLE (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人'
) COMMENT='系统角色表';

-- 3. 用户角色关联表
CREATE TABLE T_SYSTEM_USER_ROLE (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    UNIQUE KEY uk_user_role (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES T_SYSTEM_USER(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES T_SYSTEM_ROLE(id) ON DELETE CASCADE
) COMMENT='用户角色关联表';

-- 4. 权限表
CREATE TABLE T_SYSTEM_PERMISSION (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(50) NOT NULL UNIQUE COMMENT '权限编码',
    permission_type TINYINT DEFAULT 1 COMMENT '权限类型: 1-菜单, 2-按钮, 3-API',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(200) COMMENT '权限路径',
    description VARCHAR(200) COMMENT '权限描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人'
) COMMENT='系统权限表';

-- 5. 角色权限关联表
CREATE TABLE T_SYSTEM_ROLE_PERMISSION (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES T_SYSTEM_ROLE(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES T_SYSTEM_PERMISSION(id) ON DELETE CASCADE
) COMMENT='角色权限关联表';

-- 6. 用户登录日志表
CREATE TABLE T_SYSTEM_LOGIN_LOG (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    login_ip VARCHAR(50) COMMENT '登录IP',
    login_location VARCHAR(100) COMMENT '登录地点',
    browser VARCHAR(50) COMMENT '浏览器',
    os VARCHAR(50) COMMENT '操作系统',
    status TINYINT DEFAULT 1 COMMENT '登录状态: 0-失败, 1-成功',
    message VARCHAR(255) COMMENT '登录消息',
    login_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间'
) COMMENT='用户登录日志表';

-- 创建索引
CREATE INDEX idx_user_username ON T_SYSTEM_USER(username);
CREATE INDEX idx_user_email ON T_SYSTEM_USER(email);
CREATE INDEX idx_user_status ON T_SYSTEM_USER(status);
CREATE INDEX idx_role_code ON T_SYSTEM_ROLE(role_code);
CREATE INDEX idx_permission_code ON T_SYSTEM_PERMISSION(permission_code);
CREATE INDEX idx_login_log_username ON T_SYSTEM_LOGIN_LOG(username);
CREATE INDEX idx_login_log_time ON T_SYSTEM_LOGIN_LOG(login_time);

-- 插入默认角色数据
INSERT INTO T_SYSTEM_ROLE (role_name, role_code, description, create_by) VALUES
('超级管理员', 'ROLE_SUPER_ADMIN', '系统超级管理员，拥有所有权限', 'system'),
('管理员', 'ROLE_ADMIN', '系统管理员，拥有大部分权限', 'system'),
('普通用户', 'ROLE_USER', '普通用户，拥有基本权限', 'system'),
('访客', 'ROLE_GUEST', '访客用户，只读权限', 'system');

-- 插入默认权限数据
INSERT INTO T_SYSTEM_PERMISSION (permission_name, permission_code, permission_type, path, description, create_by) VALUES
('文件管理', 'file:manage', 1, '/page/', '文件管理模块', 'system'),
('文件上传', 'file:upload', 2, '/file/upload*', '文件上传权限', 'system'),
('文件下载', 'file:download', 2, '/file/*', '文件下载权限', 'system'),
('文件删除', 'file:delete', 2, '/file/delete', '文件删除权限', 'system'),
('文件列表', 'file:list', 2, '/page/file-list', '文件列表查看权限', 'system'),
('系统管理', 'system:manage', 1, '/page/system-status', '系统管理模块', 'system'),
('用户管理', 'user:manage', 2, '/admin/user*', '用户管理权限', 'system');

-- 插入默认用户数据 (密码: admin123)
INSERT INTO T_SYSTEM_USER (username, password, email, real_name, create_by) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'admin@fileserver.com', '系统管理员', 'system'),
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'user@fileserver.com', '普通用户', 'system');

-- 分配角色给用户
INSERT INTO T_SYSTEM_USER_ROLE (user_id, role_id, create_by) VALUES
(1, 1, 'system'), -- admin用户分配超级管理员角色
(2, 3, 'system'); -- user用户分配普通用户角色

-- 分配权限给角色
INSERT INTO T_SYSTEM_ROLE_PERMISSION (role_id, permission_id, create_by) VALUES
-- 超级管理员拥有所有权限
(1, 1, 'system'), (1, 2, 'system'), (1, 3, 'system'), (1, 4, 'system'), (1, 5, 'system'), (1, 6, 'system'), (1, 7, 'system'),
-- 管理员拥有大部分权限
(2, 1, 'system'), (2, 2, 'system'), (2, 3, 'system'), (2, 4, 'system'), (2, 5, 'system'), (2, 6, 'system'),
-- 普通用户拥有基本权限
(3, 1, 'system'), (3, 2, 'system'), (3, 3, 'system'), (3, 5, 'system'),
-- 访客只有查看权限
(4, 5, 'system');
