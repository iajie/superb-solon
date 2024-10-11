package com.superb.common.doc.config;

import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import com.superb.common.doc.config.properties.Knife4jProperties;
import com.superb.common.utils.HeadersUtils;
import io.swagger.models.Scheme;
import io.swagger.models.parameters.HeaderParameter;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiContact;
import org.noear.solon.docs.models.ApiInfo;
import org.noear.solon.docs.models.ApiResource;

/**
 * 整合knife4j接口文档
 * https://solon.noear.org/article/568
 * @Author: ajie
 * @CreateTime: 2024-07-25 16:05
 */
@Configuration
public class Knife4jConfig {

    /**
     * 获取knife4j配置
     */
    @Inject
    private OpenApiExtensionResolver openApiExtensionResolver;

    /**
     * 自定义knife4j配置
     */
    @Inject(value = "${knife4j.custom}", autoRefreshed = true)
    private Knife4jProperties properties;

    /**
     * 丰富点的
     * 注：bean name不能少了！！！ 一定要有，且聚合中每个服务的bean name得一样才能正常聚合
     * 不注册gateway
     */
    @Bean("default")
    public DocDocket adminApi() {
        return new DocDocket()
                .groupName(properties.getGroupName())
                .vendorExtensions(openApiExtensionResolver.buildExtensions())
                .info(new ApiInfo().title(properties.getTitle())
                        .description(properties.getDescription())
                        .contact(new ApiContact().name(properties.getAuthor())
                                .email(properties.getEmail()))
                        .version(properties.getVersion()))
                .schemes(Scheme.HTTP.toValue(), Scheme.HTTPS.toValue())
                // 接口文档请求头 name为请求header字段，description接口文档中的描述，required是否为必填项
                .globalParams(new HeaderParameter().name(HeadersUtils.token).description("登录凭证").required(false))
                .apis(new ApiResource()); //所有存在Mapping注解的方法
    }

}
