package com.superb.gateway;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.SolonMain;

/**
 * Superb Gateway网关，只做转发，不做鉴权等操作
 * @Author: ajie
 * @CreateTime: 2024-07-29 08:52
 */
@Mapping
@SolonMain
public class SuperbGatewaySolonApp {

    public static void main(String[] args) {
        Solon.start(SuperbGatewaySolonApp.class, args);
    }

}
