-- 创建数据库
CREATE DATABASE IF NOT EXISTS fileserver DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fileserver;

-- 创建文件元数据表
CREATE TABLE IF NOT EXISTS T_SYSTEM_FILE (
    id VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '文件唯一标识',
    filename VARCHAR(255) NOT NULL COMMENT '文件名',
    filepath VARCHAR(500) NOT NULL COMMENT '文件路径',
    filesize BIGINT NOT NULL COMMENT '文件大小(字节)',
    subfix VARCHAR(20) COMMENT '文件后缀',
    createtime DATETIME NOT NULL COMMENT '创建时间',
    INDEX idx_createtime (createtime),
    INDEX idx_filename (filename)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统文件表';
