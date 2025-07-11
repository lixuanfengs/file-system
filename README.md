# 🚀 FileServer - 企业级文件管理系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Jetty](https://img.shields.io/badge/Jetty-12.0.10-blue.svg)](https://www.eclipse.org/jetty/)
[![MyBatis](https://img.shields.io/badge/MyBatis-3.0.3-red.svg)](https://mybatis.org/)

基于 Spring Boot 3.x + JDK 17 的现代化企业级文件管理系统，提供文件上传、下载、预览、管理和系统监控功能。

## ✨ 核心功能

### 📁 文件管理
- **文件操作**: 单文件/多文件上传、下载、删除
- **文件预览**: 支持图片、文档(PDF/Office)、文本、Markdown等格式预览
- **缩略图**: 自动生成图片缩略图
- **元数据**: 完整的文件信息存储和查询

### 🔐 用户权限
- **用户认证**: Spring Security集成，支持登录/登出
- **权限控制**: 基于角色的访问控制(RBAC)
- **用户管理**: 用户增删改查、状态管理

### 📊 系统监控
- **实时监控**: CPU、内存、磁盘、网络等系统指标
- **业务统计**: 文件数量、用户统计等业务数据
- **动态展示**: 基于SSE的实时数据推送

### 🎨 现代化界面
- **响应式设计**: 基于Tailwind CSS的现代化UI
- **表格组件**: AG Grid集成，支持分页、排序、筛选
- **文件管理**: 统一的文件列表和管理界面

## 🏗️ 技术架构

### 技术栈
- **后端**: Spring Boot 3.3.1 + JDK 17 + Jetty 12.0.10
- **数据库**: MySQL 8.0+ + MyBatis 3.0.3 + HikariCP
- **安全**: Spring Security 6.x
- **监控**: SkyWalking APM + OSHI系统监控
- **文档转换**: JODConverter(远程模式) + LibreOffice
- **前端**: Thymeleaf + Tailwind CSS + AG Grid
- **构建**: Maven 3.x

## 🚀 快速开始

### 环境要求
- **JDK**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **LibreOffice**: 7.0+ (可选，用于文档预览，需要独立运行)

### 1. 数据库配置
```sql
CREATE DATABASE fileserver DEFAULT CHARACTER SET utf8mb4;
```

### 2. LibreOffice安装与配置
#### 安装LibreOffice
```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install libreoffice

# CentOS/RHEL
sudo yum install libreoffice

# Windows
# 下载并安装 LibreOffice 7.0+ 版本
```

#### 启动LibreOffice服务
```bash
# Linux/macOS - 以无头模式启动LibreOffice服务
soffice --headless --accept="socket,host=127.0.0.1,port=2002;urp;" --nofirststartwizard

# Windows
"C:\Program Files\LibreOffice\program\soffice.exe" --headless --accept="socket,host=127.0.0.1,port=2002;urp;" --nofirststartwizard

# 后台运行（Linux）
nohup soffice --headless --accept="socket,host=127.0.0.1,port=2002;urp;" --nofirststartwizard > /dev/null 2>&1 &
```

### 3. 配置文件
修改 `application.yml` 中的数据库连接，推荐通过环境变量或独立的 `jdbc.properties` 提供凭证：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fileserver
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```


如需使用文件方式配置，可在 `src/main/resources` 目录下创建 `jdbc.properties`，其中定义 `DB_USERNAME` 和 `DB_PASSWORD` 两个属性。

同时，你也可以在 `fileserver` 部分自定义文件存储路径和对外访问地址：
```yaml
fileserver:
  filestore: ./filestore/  # 文件存储目录
  serverhost: localhost    # 文件访问主机名
  serverport: 9100         # 文件访问端口

### 4. 运行应用
```bash
# 编译运行
mvn spring-boot:run

# 或打包运行
mvn clean package
java -jar target/fileserver-0.0.1-SNAPSHOT.jar
```

### 5. 访问系统
- **登录页面**: http://localhost:9101/login
- **默认账号**: admin / admin123
- **健康检查**: http://localhost:9101/health

## 📡 主要接口

### 文件操作
| 功能 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 文件上传 | POST | `/file/uploadOne` | 单文件上传 |
| 批量上传 | POST | `/file/uploadFileMore` | 多文件上传 |
| 文件下载 | GET | `/file/{fileKey}` | 文件下载 |
| 文件预览 | GET | `/file/viewFile/{fileKey}` | 在线预览 |
| 缩略图 | GET | `/file/viewSmallFile/{fileKey}` | 图片缩略图 |
| 文件信息 | GET | `/file/getInfo` | 获取元数据 |
| 删除文件 | POST | `/file/delete` | 删除文件 |

### 系统监控
| 功能 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 系统信息 | GET | `/api/system/info` | 基础系统信息 |
| 监控数据 | GET | `/api/system/monitor` | 实时监控数据 |
| 系统概览 | GET | `/api/system/overview` | 系统概览统计 |

## ⚙️ 配置说明

### 主要配置项
```yaml
server:
  port: 9101                    # 服务端口

spring:
  servlet:
    multipart:
      max-file-size: 200MB      # 单文件大小限制
      max-request-size: 1800MB  # 请求大小限制

fileserver:
  filestore: ./filestore/       # 文件存储目录
  serverhost: localhost         # 文件访问主机名
  serverport: 9100              # 文件访问端口

# JODConverter配置(文档预览) - 使用远程连接模式
jodconverter:
  remote:
    enabled: true                      # 启用远程连接模式
    url: "socket,host=127.0.0.1,port=2002;urp;"  # LibreOffice服务地址
```

## 📋 功能特性

### 支持的文件格式
- **图片**: JPG, PNG, GIF, BMP, WebP
- **文档**: PDF, DOC/DOCX, XLS/XLSX, PPT/PPTX
- **文本**: TXT, MD, JSON, XML, CSV
- **压缩**: ZIP, RAR, 7Z

### 预览功能
- **图片预览**: 直接显示，支持缩略图
- **文档预览**: 基于JODConverter远程连接模式转换为HTML
- **文本预览**: 语法高亮显示
- **Markdown**: 渲染为HTML格式

## 🔒 安全特性

- **用户认证**: Spring Security集成
- **权限控制**: 基于角色的访问控制
- **文件安全**: UUID命名，防止路径遍历
- **上传限制**: 文件大小和类型限制

## 📊 监控功能

- **系统监控**: CPU、内存、磁盘使用率
- **业务统计**: 文件数量、用户统计
- **实时数据**: SSE推送实时监控数据
- **APM监控**: SkyWalking链路追踪

## 🚀 部署方式

### Docker部署
```dockerfile
FROM openjdk:17-jdk-slim

# 安装LibreOffice
RUN apt-get update && \
    apt-get install -y libreoffice && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY target/fileserver-0.0.1-SNAPSHOT.jar app.jar

# 启动脚本
COPY start.sh /start.sh
RUN chmod +x /start.sh

EXPOSE 9101
ENTRYPOINT ["/start.sh"]
```

创建启动脚本 `start.sh`:
```bash
#!/bin/bash
# 启动LibreOffice服务
soffice --headless --accept="socket,host=0.0.0.0,port=2002;urp;" --nofirststartwizard &

# 等待LibreOffice启动
sleep 5

# 启动应用
java -jar /app.jar
```

### 传统部署
```bash
# 1. 启动LibreOffice服务
nohup soffice --headless --accept="socket,host=127.0.0.1,port=2002;urp;" --nofirststartwizard > /dev/null 2>&1 &

# 2. 打包运行应用
mvn clean package
java -jar target/fileserver-0.0.1-SNAPSHOT.jar
```

---

## 📄 许可证

本项目采用 MIT 许可证开源。
