# 🚀 FileServer - 企业级文件服务器

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Jetty](https://img.shields.io/badge/Jetty-12.0.10-blue.svg)](https://www.eclipse.org/jetty/)
[![MyBatis](https://img.shields.io/badge/MyBatis-3.0.3-red.svg)](https://mybatis.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

一个基于 Spring Boot 3.x + JDK 17 + Jetty 的现代化企业级文件服务器，提供完整的文件上传、下载、预览、管理功能。

## 📋 项目概述

FileServer 是一个功能完整的文件管理系统，支持单文件和多文件上传、文件预览、缩略图生成、文件元数据管理等功能。项目采用现代化的技术栈，具有高性能、高可用性和易扩展的特点。

### ✨ 核心特性

- 🔥 **现代化技术栈**: Spring Boot 3.3.1 + JDK 17 + Jetty 12.0.10
- 📁 **完整文件管理**: 上传、下载、预览、删除、元数据管理
- 🖼️ **图片处理**: 自动生成缩略图，支持多种图片格式
- 🗄️ **数据持久化**: MyBatis + MySQL，支持文件元数据存储
- 🌐 **RESTful API**: 标准化的 REST 接口设计
- 🎨 **现代化UI**: 基于 Tailwind CSS 的响应式测试界面
- ⚡ **高性能**: Jetty 服务器 + HikariCP 连接池
- 📊 **监控支持**: 集成 SkyWalking APM 监控
- 🔧 **易于配置**: YAML 配置文件，支持多环境部署

## 🏗️ 技术架构

### 技术栈
- **后端框架**: Spring Boot 3.3.1
- **Web服务器**: Jetty 12.0.10 (替代 Tomcat)
- **Java版本**: JDK 17
- **数据库**: MySQL 8.0+
- **ORM框架**: MyBatis 3.0.3
- **连接池**: HikariCP
- **构建工具**: Maven 3.x
- **监控**: SkyWalking APM
- **前端**: HTML5 + JavaScript + Tailwind CSS

### 项目结构
```
file-system/
├── src/main/java/net/cactus/
│   ├── FileServerApplication.java      # 主应用类
│   ├── config/                         # 配置类
│   │   ├── AppConfiguration.java       # 应用配置
│   │   ├── JettyConfig.java           # Jetty服务器配置
│   │   ├── MvcConfiguration.java       # MVC配置
│   │   └── ServiceConfiguration.java   # 服务配置
│   ├── controller/                     # 控制器层
│   │   ├── FileController.java         # 文件操作控制器
│   │   └── TestController.java         # 测试控制器
│   ├── service/                        # 服务层
│   │   ├── FileService.java            # 文件服务接口
│   │   ├── StorageService.java         # 存储服务接口
│   │   ├── FileMetaService.java        # 文件元数据服务接口
│   │   └── impl/                       # 服务实现类
│   ├── dao/                           # 数据访问层
│   │   └── FileMetaDao.java           # 文件元数据DAO
│   ├── pojo/                          # 实体类
│   │   └── FileMeta.java              # 文件元数据实体
│   └── utils/                         # 工具类
├── src/main/resources/
│   ├── application.yml                 # 主配置文件
│   ├── init.sql                       # 数据库初始化脚本
│   ├── logback-spring.xml             # 日志配置
│   └── org/mybatis/mapper/            # MyBatis映射文件
├── filestore/                         # 文件存储目录
├── test-upload.html                   # 测试页面
└── pom.xml                           # Maven配置
```

## 🚀 快速开始

### 环境要求

- **JDK**: 17 或更高版本
- **Maven**: 3.6+ 
- **MySQL**: 8.0+
- **操作系统**: Windows/Linux/macOS

### 1. 克隆项目
```bash
git clone <repository-url>
cd file-system
```

### 2. 数据库配置

#### 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS fileserver DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 执行初始化脚本
```bash
mysql -u root -p fileserver < src/main/resources/init.sql
```

#### 数据库表结构
```sql
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
```

### 3. 配置文件

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/fileserver?useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 4. 编译和运行

#### 开发环境
```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

#### 生产环境
```bash
# 打包项目
mvn clean package

# 运行JAR文件
java --add-opens java.base/java.lang=ALL-UNNAMED \
     --add-opens java.base/java.util=ALL-UNNAMED \
     -jar target/fileserver-0.0.1-SNAPSHOT.jar
```

### 5. 访问应用

- **应用地址**: http://localhost:9101
- **健康检查**: http://localhost:9101/health
- **测试页面**: 打开 `test-upload.html` 文件

## 📡 API 接口

### 健康检查
```http
GET /health
```

### 文件操作接口

| 接口 | 方法 | 路径 | 功能 | 参数 |
|------|------|------|------|------|
| 单文件上传 | POST | `/file/uploadOne` | 上传单个文件 | `file`: MultipartFile |
| 多文件上传 | POST | `/file/uploadFileMore` | 上传多个文件 | `files`: MultipartFile[] |
| 文件下载 | GET | `/file/{fileKey}` | 下载文件 | `fileKey`: 文件唯一标识 |
| 文件预览 | GET | `/file/viewFile/{fileKey}` | 预览文件 | `fileKey`: 文件唯一标识 |
| 缩略图预览 | GET | `/file/viewSmallFile/{fileKey}` | 预览缩略图 | `fileKey`: 文件唯一标识 |
| 文件信息 | GET | `/file/getInfo` | 获取文件元数据 | `fileKey`: 文件唯一标识 |
| 删除文件 | POST | `/file/delete` | 删除文件 | `fileKey`: 文件唯一标识 |

### 响应格式
```json
{
  "success": true,
  "code": 0,
  "message": "SUCCESS",
  "result": "文件UUID或具体数据"
}
```

## 🔧 配置说明

### 应用配置 (application.yml)

```yaml
# 服务器配置
server:
  port: 9101                    # 服务端口
  address: localhost            # 服务地址
  
# 文件上传配置
spring:
  servlet:
    multipart:
      max-file-size: 200MB      # 单文件最大大小
      max-request-size: 1800MB  # 请求最大大小
      
# 自定义配置
fileserver:
  filestore: ./filestore/       # 文件存储路径
  serverhost: localhost         # 服务器主机
  serverport: 9100             # 服务器端口
```

### Jetty 服务器配置

项目使用 Jetty 替代默认的 Tomcat：

```yaml
server:
  jetty:
    connection-idle-timeout: 1200000ms
    max-http-form-post-size: 200MB
    threads:
      max: 200                  # 最大线程数
      min: 8                    # 最小线程数
      idle-timeout: 60000ms     # 线程空闲超时
```

## 🧪 功能测试

### 使用测试页面

1. 在浏览器中打开 `test-upload.html`
2. 测试各项功能：
   - 健康检查
   - 单文件上传
   - 多文件上传
   - 文件信息查询
   - 文件下载和预览
   - 文件删除

### 使用 cURL 测试

```bash
# 健康检查
curl http://localhost:9101/health

# 文件上传
curl -X POST -F "file=@test.txt" http://localhost:9101/file/uploadOne

# 获取文件信息
curl "http://localhost:9101/file/getInfo?fileKey=your-file-key"

# 下载文件
curl -O http://localhost:9101/file/your-file-key
```

## 🎯 核心功能

### 文件存储
- 支持本地文件系统存储
- 自动创建分层目录结构
- UUID 文件命名，避免冲突
- 支持文件扩展名保留

### 图片处理
- 自动识别图片格式 (JPG, PNG, GIF)
- 自动生成 247x247 缩略图
- 支持图片预览和缩略图预览

### 数据管理
- 文件元数据持久化存储
- 支持文件信息查询
- 支持文件删除（物理删除+数据库删除）

## 📊 监控和日志

### SkyWalking 监控
项目集成了 SkyWalking APM 监控：

```xml
<dependency>
    <groupId>org.apache.skywalking</groupId>
    <artifactId>apm-toolkit-trace</artifactId>
    <version>8.12.0</version>
</dependency>
```

### 日志配置
使用 Logback 进行日志管理，配置文件：`src/main/resources/logback-spring.xml`

## 🔒 安全考虑

### 建议的安全措施
1. **文件类型验证**: 限制允许上传的文件类型
2. **文件大小限制**: 已配置最大文件大小限制
3. **访问控制**: 建议添加用户认证和授权
4. **路径遍历防护**: 使用 UUID 文件名避免路径遍历攻击
5. **病毒扫描**: 建议集成病毒扫描功能

## 🚀 部署指南

### Docker 部署 (推荐)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/fileserver-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9101
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "--add-opens", "java.base/java.util=ALL-UNNAMED", "-jar", "/app.jar"]
```

### 传统部署
1. 编译打包项目
2. 上传 JAR 文件到服务器
3. 配置数据库连接
4. 启动应用

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- 项目地址: [GitHub Repository]
- 问题反馈: [Issues]

---

⭐ 如果这个项目对您有帮助，请给它一个星标！
