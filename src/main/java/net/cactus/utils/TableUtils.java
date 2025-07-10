package net.cactus.utils;

import net.cactus.annotation.TableColumn;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class TableUtils {

    /**
     * Generates a list of column definitions from a class's @TableColumn annotations.
     *
     * @param clazz The class to inspect.
     * @return A sorted list of maps, where each map represents a column definition.
     */
    public static List<Map<String, Object>> generateColumns(Class<?> clazz) {
        // Find all fields that have the @TableColumn annotation
        List<Field> annotatedFields = Arrays.stream(clazz.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(TableColumn.class))
            .sorted(Comparator.comparingInt(f -> f.getAnnotation(TableColumn.class).order()))
            .collect(Collectors.toList());

        // Create a map for each annotated field
        List<Map<String, Object>> columns = new ArrayList<>();
        for (Field field : annotatedFields) {
            TableColumn annotation = field.getAnnotation(TableColumn.class);
            Map<String, Object> columnMap = new LinkedHashMap<>(); // Use LinkedHashMap to preserve key order

            // Use the explicit key from the annotation, or fall back to the field name
            String key = annotation.key().isEmpty() ? field.getName() : annotation.key();

            columnMap.put("key", key);
            columnMap.put("label", annotation.label());
            columnMap.put("type", annotation.type());
            columnMap.put("defaultVisible", annotation.visible());
            columns.add(columnMap);
        }
        return columns;
    }
}
