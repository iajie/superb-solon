package com.superb.system;

import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;

/**
 * 系统启动类
 * @Author: ajie
 * @CreateTime: 2024-07-26 14:28
 */
@SolonMain
public class SuperbSystemSolonApp {

    public static void main(String[] args) {
        Solon.start(SuperbSystemSolonApp.class, args);
    }

}
