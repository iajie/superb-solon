package com.superb.common.utils;

import org.smartboot.http.common.utils.AntPathMatcher;

import java.util.Collection;
import java.util.Set;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-29 13:08
 */
public class PatternParserUtils {


    public static boolean isMatch(Collection<String> patterns, String path) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        if (patterns == null || patterns.isEmpty()) {
            return false;
        }
        for (String pattern : patterns) {
            if (antPathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

}
