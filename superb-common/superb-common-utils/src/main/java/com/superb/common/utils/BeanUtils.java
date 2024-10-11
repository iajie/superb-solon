package com.superb.common.utils;

import com.superb.common.core.exception.SuperbException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-07 15:01
 */
public class BeanUtils {

    /**
     * 获取实体类属性名称列表
     * @param clazz 实体类
     * @return
     */
    public static String[] getClassFields(Class<?> clazz, String ...removeFields) {
        List<String> list = Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
        for (String removeField : removeFields) {
            list.removeIf(removeField::equals);
        }
        return list.toArray(String[]::new);
    }

    /**
     * 获取实体类属性名称列表
     * @param clazz 实体类
     * @return
     */
    public static List<String> getClassFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
    }

    /**
     * 获取实体类属性名称列表
     * @param t 实体类
     * @return
     */
    public static <T> Map<String, Object> getClassFields(T t) {
        Map<String, Object> map = new HashMap<>();
        for (Field field : t.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(t));
            } catch (IllegalAccessException e) {
                throw new SuperbException("泛型类转map异常", e);
            }
        }
        return map;
    }

}
