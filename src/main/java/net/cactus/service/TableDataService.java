package net.cactus.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import net.cactus.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表格数据处理服务
 * 提供通用的表格数据转换和处理功能
 */
@Service
public class TableDataService {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 将实体列表转换为表格数据
     * 
     * @param entities 实体列表
     * @param entityClass 实体类
     * @param <T> 实体类型
     * @return 表格数据和列定义
     */
    public <T> TableData<Map<String, Object>> convertToTableData(List<T> entities, Class<T> entityClass) {
        // 转换数据
        List<Map<String, Object>> tableData = entities.stream()
                .map(entity -> (Map<String, Object>) objectMapper.convertValue(entity, Map.class))
                .collect(Collectors.toList());
        
        // 生成列定义
        List<Map<String, Object>> columns = TableUtils.generateColumns(entityClass);
        
        return new TableData<>(tableData, columns);
    }

    /**
     * 将分页实体数据转换为表格分页数据
     *
     * @param pageInfo 分页信息
     * @param entityClass 实体类
     * @param <T> 实体类型
     * @return 表格分页数据
     */
    public <T> PageInfo<Map<String, Object>> convertToTablePageData(PageInfo<T> pageInfo, Class<T> entityClass) {
        // 转换数据
        List<Map<String, Object>> tableData = pageInfo.getList().stream()
                .map(entity -> (Map<String, Object>) objectMapper.convertValue(entity, Map.class))
                .collect(Collectors.toList());

        // 创建新的分页信息，不使用构造函数避免重新计算分页信息
        PageInfo<Map<String, Object>> result = new PageInfo<>();
        result.setList(tableData);

        // 复制所有分页信息
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setSize(pageInfo.getSize());
        result.setStartRow(pageInfo.getStartRow());
        result.setEndRow(pageInfo.getEndRow());
        result.setHasPreviousPage(pageInfo.isHasPreviousPage());
        result.setHasNextPage(pageInfo.isHasNextPage());
        result.setIsFirstPage(pageInfo.isIsFirstPage());
        result.setIsLastPage(pageInfo.isIsLastPage());
        result.setPrePage(pageInfo.getPrePage());
        result.setNextPage(pageInfo.getNextPage());
        result.setNavigatePages(pageInfo.getNavigatePages());
        result.setNavigatepageNums(pageInfo.getNavigatepageNums());
        result.setNavigateFirstPage(pageInfo.getNavigateFirstPage());
        result.setNavigateLastPage(pageInfo.getNavigateLastPage());

        // 确保关键分页属性正确设置
        if (result.getPages() > 0) {
            result.setHasPreviousPage(result.getPageNum() > 1);
            result.setHasNextPage(result.getPageNum() < result.getPages());
            result.setIsFirstPage(result.getPageNum() == 1);
            result.setIsLastPage(result.getPageNum() == result.getPages());

            // 设置上一页和下一页页码
            if (result.isHasPreviousPage()) {
                result.setPrePage(result.getPageNum() - 1);
            }
            if (result.isHasNextPage()) {
                result.setNextPage(result.getPageNum() + 1);
            }
        }

        return result;
    }

    /**
     * 表格数据包装类
     */
    public static class TableData<T> {
        private List<T> data;
        private List<Map<String, Object>> columns;

        public TableData(List<T> data, List<Map<String, Object>> columns) {
            this.data = data;
            this.columns = columns;
        }

        public List<T> getData() {
            return data;
        }

        public void setData(List<T> data) {
            this.data = data;
        }

        public List<Map<String, Object>> getColumns() {
            return columns;
        }

        public void setColumns(List<Map<String, Object>> columns) {
            this.columns = columns;
        }
    }
}
