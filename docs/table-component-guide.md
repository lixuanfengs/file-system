# 通用动态表格组件使用指南

## 概述

本项目提供了一个完全封装的通用动态表格组件，支持数据展示、列设置、批量操作、分页等功能。只需要简单的配置就可以在任何页面中使用。

## 核心特性

- ✅ **完全封装** - 只需引入组件即可使用
- ✅ **自动列生成** - 基于实体类注解自动生成列定义
- ✅ **响应式设计** - 适配各种屏幕尺寸
- ✅ **批量操作** - 支持批量选择、下载、删除
- ✅ **列显示设置** - 用户可自定义显示的列
- ✅ **数据格式化** - 支持文件大小、日期、标签等格式化
- ✅ **分页支持** - 完整的分页功能
- ✅ **搜索功能** - 支持关键词搜索

## 快速开始

### 1. 实体类配置

在你的实体类字段上添加 `@TableColumn` 注解：

```java
@Entity
public class MyEntity {
    
    @TableColumn(key = "id", label = "ID", order = 1, visible = false)
    private Long id;
    
    @TableColumn(key = "name", label = "名称", order = 2)
    private String name;
    
    @TableColumn(key = "size", label = "大小", type = "size", order = 3)
    private Long size;
    
    @TableColumn(key = "createTime", label = "创建时间", type = "date", order = 4)
    private Date createTime;
    
    @TableColumn(key = "status", label = "状态", type = "badge", order = 5)
    private String status;
    
    // getter/setter...
}
```

### 2. 控制器配置

```java
@Controller
public class MyController {
    
    @Autowired
    private TableDataService tableDataService;
    
    @GetMapping("/my-list")
    public String myList(@RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {
        
        // 查询数据
        Page<MyEntity> pageResult = PageHelper.startPage(page, size);
        List<MyEntity> entities = myService.findAll();
        PageInfo<MyEntity> originalPageInfo = new PageInfo<>(entities);
        
        // 转换为表格数据
        PageInfo<Map<String, Object>> tablePageData = 
            tableDataService.convertToTablePageData(originalPageInfo, MyEntity.class);
        
        // 设置模型属性
        model.addAttribute("pageData", tablePageData);
        model.addAttribute("columns", TableUtils.generateColumns(MyEntity.class));
        
        return "pages/my-list";
    }
}
```

### 3. 页面模板

```html
<!DOCTYPE html>
<html lang="zh-CN" xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/main}">

<head>
    <title layout:fragment="title">我的列表</title>
</head>

<body>
<div layout:fragment="content">
    <div class="max-w-7xl mx-auto px-6 py-8">
        
        <!-- 页面头部 -->
        <div class="mb-8">
            <h1 class="text-3xl font-bold text-gray-900">我的列表</h1>
            <p class="text-gray-600 mt-2">管理我的数据</p>
        </div>

        <!-- 动态表格组件 -->
        <div th:replace="~{fragments/reusable-table :: dynamicTable(
                tableId='myTable',
                tableTitle='数据列表',
                pageData=${pageData},
                columns=${columns},
                actionBaseUrl='/my-action',
                showBatchActions=true,
                showColumnSettings=true
            )}">
        </div>

        <!-- 分页组件 -->
        <div th:replace="~{fragments/pagination :: pagination(
                pageData=${pageData},
                baseUrl='/my-list',
                extraParams='',
                showPageSize=true
            )}">
        </div>
    </div>
</div>

<!-- 脚本 -->
<script layout:fragment="scripts" th:src="@{/js/reusable-table.js}"></script>

</body>
</html>
```

## 组件参数说明

### 动态表格组件参数

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| tableId | String | ✅ | - | 表格唯一标识 |
| tableTitle | String | ✅ | - | 表格标题 |
| pageData | PageInfo | ✅ | - | 分页数据对象 |
| columns | List | ✅ | - | 列定义数组 |
| actionBaseUrl | String | ✅ | - | 操作基础URL |
| showBatchActions | Boolean | ❌ | true | 是否显示批量操作 |
| showColumnSettings | Boolean | ❌ | true | 是否显示列设置 |

### 分页组件参数

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| pageData | PageInfo | ✅ | - | 分页数据对象 |
| baseUrl | String | ✅ | - | 基础URL |
| extraParams | String | ❌ | '' | 额外参数 |
| showPageSize | Boolean | ❌ | false | 是否显示页面大小选择器 |

### @TableColumn 注解参数

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| key | String | ❌ | 字段名 | 属性键名 |
| label | String | ✅ | - | 列显示名称 |
| type | String | ❌ | "default" | 列类型：default/size/date/badge |
| order | int | ✅ | - | 显示顺序 |
| visible | boolean | ❌ | true | 是否默认显示 |

## 支持的列类型

### 1. default（默认文本）
```java
@TableColumn(key = "name", label = "名称", type = "default")
private String name;
```

### 2. size（文件大小）
```java
@TableColumn(key = "fileSize", label = "大小", type = "size")
private Long fileSize;
```

### 3. date（日期时间）
```java
@TableColumn(key = "createTime", label = "创建时间", type = "date")
private Date createTime;
```

### 4. badge（标签）
```java
@TableColumn(key = "status", label = "状态", type = "badge")
private String status;
```

## 自定义样式

组件使用了内联CSS样式，你可以通过以下方式自定义：

1. **覆盖CSS类**：在你的页面中添加自定义样式
2. **修改组件模板**：直接修改 `fragments/reusable-table.html`
3. **扩展JavaScript**：修改 `js/reusable-table.js` 添加新功能

## 最佳实践

1. **实体类设计**：合理使用 `@TableColumn` 注解，设置合适的 `order` 和 `visible`
2. **性能优化**：对于大数据量，建议使用数据库分页而不是内存分页
3. **用户体验**：为重要操作添加确认对话框
4. **响应式设计**：确保表格在移动设备上的显示效果
5. **错误处理**：添加适当的错误处理和用户提示

## 示例项目

项目中已经包含了完整的示例：

- **文件列表**：`/page/file-list` - 展示文件管理功能
- **用户列表**：`/page/user-list` - 展示用户管理功能（示例）

## 扩展功能

### 添加新的列类型

1. 在 `reusable-table.html` 中添加新的 `th:case`
2. 在 `reusable-table.js` 中添加相应的处理逻辑
3. 更新文档说明

### 添加新的批量操作

1. 在模板中添加新的按钮
2. 在JavaScript中添加事件处理
3. 在后端添加相应的API接口

## 故障排除

### 常见问题

1. **表格不显示**：检查 `tableId` 是否唯一，数据是否正确传递
2. **分页不工作**：检查 `baseUrl` 和 `extraParams` 是否正确
3. **批量操作失败**：检查 `actionBaseUrl` 和后端API是否匹配
4. **样式异常**：检查CSS是否被其他样式覆盖

### 调试技巧

1. 使用浏览器开发者工具检查DOM结构
2. 查看控制台错误信息
3. 检查网络请求是否正常
4. 验证数据格式是否正确

## 更新日志

- **v1.0.0** - 初始版本，支持基本的表格功能
- **v1.1.0** - 添加分页组件和搜索功能
- **v1.2.0** - 优化样式和用户体验
