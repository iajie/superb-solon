package com.superb.allocation.utils;

import com.superb.common.core.enums.SuperbCode;
import com.superb.common.core.exception.SuperbException;
import com.superb.common.utils.HeadersUtils;
import org.noear.snack.core.utils.DateUtil;
import org.noear.solon.boot.web.OutputUtils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Result;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * aliyun-oos
 * https://solon.noear.org/article/150
 * @Author: ajie
 * @CreateTime: 2024-07-04 15:25
 */
public class AliyunOssUtils {

    private static String getBucket() {
        return HeadersUtils.getHeader("bucket", () -> "zlgyl");
    }

    /**
     * 是否覆盖
     * @return
     */
    private static boolean isCover() {
        boolean isCover = true;
        String cover = HeadersUtils.getHeader("isCover", () -> "true");
        if (!"true".equals(cover)) {
            isCover = false;
        }
        return isCover;
    }


    /**
     * 上传
     * @return
     */
    public static String upload(String filename, Media media) {
        String objectName = getFilename(filename);
        if (CloudClient.file().exists(getBucket(), objectName)) {
            if (!isCover()) {
                throw new SuperbException(SuperbCode.ALIYUN_OSS_REPEAT, objectName);
            }
        }
        CloudClient.file().put(getBucket(), objectName, media);
        return objectName;
    }

    /**
     * 删除
     * @return
     */
    public static boolean delete(String key) {
        Result<?> delete = CloudClient.file().delete(getBucket(), key);
        return delete != null && delete.getCode() == 200;
    }

    /**
     * 获取文件名
     * @param filename 原始文件名
     * @return
     */
    private static String getFilename(String filename) {
        String dir = HeadersUtils.getHeader("dir", () -> "default");
        boolean isCover = true;
        String cover = HeadersUtils.getHeader("isCover", () -> "true");
        if (!"true".equals(cover)) {
            isCover = false;
        }
        String starFilename = dir + "/" + DateUtil.format(new Date(), "yyyy-MM-dd") + "/";
        if (isCover) {
            String suffix = filename.substring(filename.lastIndexOf("."));
            return starFilename + UUID.randomUUID().toString().replace("-", "") + suffix;
        }
        return starFilename + filename;
    }

    /**
     * 私有对象获取公网url
     * @return
     */
    public static String getUrl(String key) {
        // 指定生成的签名URL过期时间，单位为毫秒。本示例以设置过期时间为1小时为例。
        Date expiration = new Date(new Date().getTime() + 3600 * 1000L);
        return CloudClient.file().getTempUrl(getBucket(), key, expiration);
    }

    /**
     * 预览
     * @return
     */
    public static void view(String key) {
        Media media = CloudClient.file().get(getBucket(), key);
        try {
            OutputUtils.global().outputStream(Context.current(), media.body(), media.contentSize(), media.contentType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
