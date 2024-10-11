package com.superb.common.doc.config.properties;

import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-07-25 16:06
 */
@Data
public class Knife4jProperties {

    /**
     * 服务网文档分组名称
     */
    private String groupName;

    /**
     * 文档标题
     */
    private String title = "superb接口文档";

    /**
     * 文档描述
     */
    private String description = "superb接口描述";

    /**
     * 版本
     */
    private String version = "v1.0";

    /**
     * 作者
     */
    private String author = "ajie";

    /**
     * 联系邮箱地址
     */
    private String email = "ajie-superb@163.com";
}
