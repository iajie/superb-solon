package com.superb.gateway.handler;

import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.exception.SuperbException;
import com.superb.common.core.model.Result;
import com.superb.common.utils.AuthDataScopeUtils;
import com.superb.common.utils.HeadersUtils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * 请求结束处理
 * 出现异常情况
 * @Author: ajie
 * @CreateTime: 2024-07-29 11:23
 */
public class AfterHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Throwable {
        Throwable errors = ctx.errors;
        if (errors != null) {
            ctx.setHandled(true);
            if (errors instanceof SuperbException ee) {
                if (ee.getCode() != null) {
                    ctx.render(Result.error(ee.getCode(), ee.getMessage()));
                } else {
                    ctx.render(Result.error(ee.getMessage()));
                }
            } else if (errors instanceof IllegalStateException ee){
                String message = ee.getMessage();
                String str = "No service address found";
                if (message.contains(str)) {
                    ctx.render(Result.error(SuperbCode.REQUEST_HEADERS_ERROR, "请检查服务["+ message.substring(str.length() + 2) +"]是否启动！"));
                } else {
                    ctx.render(Result.error(SuperbCode.REQUEST_HEADERS_ERROR, errors.getMessage()));
                }
            } else {
                ctx.render(Result.error(SuperbCode.REQUEST_HEADERS_ERROR, errors.getMessage()));
            }
        }
        // 清除线程数据
        AuthDataScopeUtils.clear();
        HeadersUtils.clear();
    }
}
