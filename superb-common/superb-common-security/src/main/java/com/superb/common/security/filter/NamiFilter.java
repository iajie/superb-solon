package com.superb.common.security.filter;

import com.superb.common.utils.HeadersUtils;
import com.superb.common.utils.InnerIpUtils;
import com.superb.common.utils.IpUtils;
import org.noear.nami.Filter;
import org.noear.nami.Invocation;
import org.noear.nami.Result;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextUtil;

/**
 * feign配置
 * @Author: ajie
 * @CreateTime: 2024-07-26 09:46
 */
public interface NamiFilter extends Filter {

    /**
     * rpc远程调用过滤器重写，带内部调用信息
     * @param inv
     * @return
     * @throws Throwable
     */
    default Result doFilter(Invocation inv) throws Throwable {
        // 内部调用标记
        inv.headers.put(HeadersUtils.feign, "true");
        inv.headers.put(IpUtils.X_SUPERB_INNER_IP, InnerIpUtils.getLocalIp());
        inv.headers.put(IpUtils.X_SUPERB_IP, IpUtils.getCustomIp());
        Context cxt = ContextUtil.current();
        if (cxt != null) {
            NvMap headerMap = cxt.headerMap();
            headerMap.forEach((headerName, headerValue) -> inv.headers.put(headerName, cxt.header(headerName)));
        }
        return inv.invoke();
    }

}
