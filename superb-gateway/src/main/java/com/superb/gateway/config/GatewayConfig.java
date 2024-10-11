package com.superb.gateway.config;

import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.model.Result;
import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.StringUtils;
import com.superb.gateway.handler.AfterHandler;
import com.superb.gateway.handler.HeaderHandler;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.cloud.utils.http.HttpUtils;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Gateway;
import org.smartboot.http.common.enums.HttpMethodEnum;

/**
 * gateway网关
 * https://solon.noear.org/article/212
 * @Author: ajie
 * @CreateTime: 2024-07-29 08:54
 */
@Mapping("/**")
@Component
public class GatewayConfig extends Gateway {

    @Override
    protected void register() {
        // 校验header
        before(HeaderHandler.class);
        after(new AfterHandler());
        // 添加服务转发
        add(Nav.class);
    }



    public static class Nav {
        //没有加印射值时，将做为默认处理 //当只有默认处理时，将接收所有请求
        @Mapping
        public Object def(Context ctx) throws Throwable {
            //检测请求，并尝试获取接口服务名
            String serverName = ctx.pathMap("/{serverName}/**").get("serverName");
            if (serverName == null) {
                ctx.setHandled(true);
                ctx.render(Result.error(SuperbCode.AUTH_404));
                return ctx;
            }
            if (!HttpMethodEnum.GET.getMethod().equals(ctx.method()) && !HttpMethodEnum.POST.getMethod().equals(ctx.method())) {
                ctx.setHandled(true);
                ctx.render(Result.error("请求异常：不被允许的请求'"+ ctx.method() +"'"));
                return ctx;
            }
            // 获取服务真实路径 -- 获取url参数，解决转发参数丢失问题
            String params = ctx.queryString();
            String servicePath = ctx.path().substring(serverName.length() + 1);
            if (StringUtils.isNotBlank(params)) {
                servicePath = servicePath + "?" + params;
            }
            NvMap nvMap = ctx.headerMap();
            // 追加gateway请求头，可以到子服务中判断是否由gateway转发
            nvMap.put(HeadersUtils.gateway, "superb-gateway");
            //转发请求（分布式的特点：转发到别处去）//使用服务名转发，即是用“负载均衡”了
            return HttpUtils.http(serverName, servicePath).headers(nvMap).bodyJson(ctx.body()).execAsBody(ctx.method());
        }
    }


    /**
     * @author: ajie
     * @date: 2024/8/19 9:53
     * @description: gateway过滤器配置
     */
//    @Override
//    public Mono<Void> doFilter(ExContext ctx, ExFilterChain chain) {
//        try {
//            // 校验请求头
//            String tenantId = ctx.rawHeader(HeadersUtils.tenantId);
//            String clientId = ctx.rawHeader(HeadersUtils.clientId);
//            String token = ctx.rawHeader(HeadersUtils.token);
//            String path = ctx.rawPath();
//            if (PatternParserUtils.isMatch(ignoreUrl, path)) {
//                if (StringUtils.isBlank(tenantId)) {
//                    throw new SuperbException(SuperbCode.REQUEST_ERROR, "无租户凭证");
//                } else if (StringUtils.isBlank(clientId)) {
//                    throw new SuperbException(SuperbCode.REQUEST_ERROR, "无客户端凭证");
//                } else if (StringUtils.isNotBlank(clientId)) {
//                    DeviceType of = DeviceType.of(clientId);
//                } else if (StringUtils.isBlank(token)) {
//                    // 校验地址是否白名单 nacos
//                    if (PatternParserUtils.isMatch(ignoreServiceUrl, path)) {
//                        throw new SuperbException(SuperbCode.AUTH_ERROR, "无登录凭证");
//                    }
//                }
//            }
//            // 追加gateway请求头，可以到子服务中判断是否由gateway转发
//            ctx.newRequest().headerAdd(HeadersUtils.gateway, "superb-gateway");
//            return chain.doFilter(ctx);
//        } catch (Throwable errors) {
//            errors.printStackTrace();
//            ctx.newResponse().header("Content-Type", "application/json;charset=UTF-8");
//            Result<String> error = Result.error(SuperbCode.REQUEST_HEADERS_ERROR, errors.getMessage());
//            // 异常处理
//            if (errors instanceof SuperbException ee) {
//                if (ee.getCode() != null) {
//                    error = Result.error(ee.getCode(), ee.getMessage());
//                } else {
//                    error = Result.error(ee.getMessage());
//                }
//            } else if (errors instanceof IllegalStateException ee){
//                String message = ee.getMessage();
//                String str = "No service address found";
//                if (message.contains(str)) {
//                    error = Result.error(SuperbCode.REQUEST_HEADERS_ERROR, "请检查服务["+ message.substring(str.length() + 2) +"]是否启动！");
//                }
//            }
//            String resultStr = ONode.stringify(error);
//            ctx.newResponse().body(Buffer.buffer(resultStr));
//            return Mono.empty();
//        }
//    }
}
