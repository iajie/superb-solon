package com.superb.common.utils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-07 15:12
 */
public class StringUtils {

    public static boolean isEmpty(Object obj) {
        return Objects.isNull(obj);
    }

    public static boolean isNotEmpty(Object obj) {
        return !Objects.isNull(obj);
    }

    /**
     * 判断字符串是否为空 “” ''两种都算
     * @param obj
     * @return
     */
    public static boolean isNotBlank(Object obj) {
        if (Objects.isNull(obj)) {
            return false;
        }
        return !"".equals(obj.toString()) && !"\"\"".equals(obj.toString()) && !"''".equals(obj.toString());
    }

    /**
     * 判断字符串是否为空 “” ''两种都算
     * @param obj
     * @return
     */
    public static boolean isBlank(Object obj) {
        if (Objects.isNull(obj)) {
            return true;
        }
        return "".equals(obj.toString()) || "\"\"".equals(obj.toString()) || "''".equals(obj.toString());
    }

    /**
     * 获取字符串在规则中出现的第几次位置下标
     * @param regex 规则
     * @param str 字符串
     * @param number 出现位置
     * @return 位置下标
     */
    public static int getIndex(String regex, String str, int number) {
        Matcher slashMatcher = Pattern.compile(regex).matcher(str);
        int mIdx = 0;
        while(slashMatcher.find()) {
            mIdx++;
            if(mIdx == number){
                break;
            }
        }
        return slashMatcher.start();
    }

}
