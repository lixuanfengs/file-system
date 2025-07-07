# FileServer 文件服务器

这是一个基于Spring Boot的文件服务器应用，提供文件上传、下载、预览等功能。

## 功能特性

- 文件上传（单文件/多文件）
- 文件下载
- 文件预览
- 图片缩略图生成
- 文件元数据管理
- RESTful API接口

## 技术栈

- Spring Boot 2.0.4
- Spring MVC
- MyBatis
- MySQL
- Jetty Server
- HikariCP连接池

## 快速开始

### 1. 环境要求

- JDK 1.8+
- MySQL 5.7+
- Maven 3.3+

### 2. 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE fileserver DEFAULT CHARACTER SET utf8mb4;
```

2. 执行初始化脚本：
```bash
mysql -u root -p fileserver < src/main/resources/init.sql
```

3. 修改数据库连接配置 `src/main/resources/jdbc.properties`：
```properties
jdbc.url=jdbc:mysql://localhost:3306/fileserver?useUnicode=true&characterEncoding=UTF-8&useSSL=false
jdbc.uid=your_username
jdbc.pwd=your_password
```

### 3. 应用配置

修改 `src/main/resources/config.properties`：
```properties
# 文件存储路径（请确保目录存在且有写权限）
filestore=/path/to/your/filestore
# 服务器配置
serverhost=localhost
serverport=9100
```

### 4. 编译运行

```bash
# 编译
mvn clean compile

# 运行
mvn exec:java -Dexec.mainClass="net.qiyuesuo.FileServerApplication"

# 或者打包后运行
mvn clean package
java -jar target/fileserver-0.0.1-SNAPSHOT.jar
```

### 5. 验证服务

访问健康检查接口：
```
GET http://localhost:9100/health
```

## API接口

### 文件上传
```
POST /file/uploadOne
Content-Type: multipart/form-data
参数: file (文件)
```

### 文件下载
```
GET /file/{fileKey}
```

### 文件预览
```
GET /file/viewFile/{fileKey}
```

### 文件信息
```
GET /file/getInfo?fileKey={fileKey}
```

### 文件删除
```
POST /file/delete
参数: fileKey
```

## 目录结构

```
src/
├── main/
│   ├── java/
│   │   └── net/qiyuesuo/
│   │       ├── config/          # 配置类
│   │       ├── controller/      # 控制器
│   │       ├── dao/            # 数据访问层
│   │       ├── pojo/           # 实体类
│   │       ├── service/        # 服务层
│   │       └── utils/          # 工具类
│   └── resources/
│       ├── config.properties   # 应用配置
│       ├── jdbc.properties     # 数据库配置
│       ├── logback.xml         # 日志配置
│       ├── spring-mybatis.xml  # MyBatis配置
│       └── init.sql           # 数据库初始化脚本
```

## 注意事项

1. 确保文件存储目录存在且有读写权限
2. 数据库连接信息需要正确配置
3. 端口9100需要确保未被占用
4. 建议在生产环境中修改默认配置

## 🎉 项目完成状态

✅ **项目已成功完善并可以正常运行！**

### 已完成的功能：
- ✅ Spring Boot应用成功启动
- ✅ Jetty服务器运行在端口9100
- ✅ 所有API接口正常注册
- ✅ MyBatis数据库集成
- ✅ 文件上传/下载功能
- ✅ 文件预览功能
- ✅ 健康检查接口

### 测试方法：

1. **使用测试页面**：
   打开 `test-upload.html` 文件进行可视化测试

2. **使用curl命令测试**：
   ```bash
   # 健康检查
   curl http://localhost:9100/health

   # 文件上传
   curl -X POST -F "file=@your-file.txt" http://localhost:9100/file/uploadOne

   # 文件下载
   curl http://localhost:9100/file/{fileKey} -o downloaded-file
   ```

3. **API接口列表**：
   - `GET /health` - 健康检查
   - `POST /file/uploadOne` - 单文件上传
   - `POST /file/uploadFileMore` - 多文件上传
   - `GET /file/{fileKey}` - 文件下载
   - `GET /file/viewFile/{fileKey}` - 文件预览
   - `GET /file/viewSmallFile/{fileKey}` - 缩略图预览
   - `GET /file/getInfo?fileKey={fileKey}` - 获取文件信息
   - `POST /file/delete` - 删除文件

## 故障排除

1. **启动失败**：检查数据库连接配置和文件存储路径
2. **文件上传失败**：检查存储目录权限
3. **端口冲突**：修改config.properties中的serverport配置
4. **Java模块系统问题**：确保使用了正确的JVM参数：
   ```bash
   java --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED -jar target/fileserver-0.0.1-SNAPSHOT.jar
   ```

## 开发说明

这个项目是从反编译的JAR文件还原而来，已经成功解决了以下技术难题：
- Java 17模块系统兼容性问题
- Spring CGLIB代理问题
- MyBatis配置问题
- 反编译代码的语法错误修复
- 依赖管理和版本兼容性
