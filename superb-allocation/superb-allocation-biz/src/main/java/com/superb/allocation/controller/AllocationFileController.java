package com.superb.allocation.controller;

import com.superb.allocation.utils.AliyunOssUtils;
import com.superb.common.core.model.Result;
import com.superb.common.database.annotation.SuperbDataScope;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.validation.annotation.Valid;

/**
 * 文件存储;(allocation_file)表控制层
 * https://solon.noear.org/article/313
 * @Author: ajie
 * @CreateTime: 2024-7-4
 */
@Valid
@Controller
@SuperbDataScope
@Mapping("/file")
@Api("文件存储接口")
public class AllocationFileController {

    @Mapping(value = "upload", method = MethodType.POST)
    @ApiOperation(value = "文件上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dir", value = "存到存储空间中的路径默认存在default文件夹下，以天分割开", paramType = "header", dataTypeClass = String.class),
            @ApiImplicitParam(name = "bucket", value = "存到哪个存储空间：默认zlgyl", paramType = "header", dataTypeClass = String.class),
            @ApiImplicitParam(name = "auth", value = "文件是否授权，true为私有需授权访问，false为公共读任何人（包括匿名访问者）都可以对该文件进行读操作", paramType = "header", dataTypeClass = String.class),
            @ApiImplicitParam(name = "isCover", value = "文件是否覆盖，true当文件重复时，直接覆盖图片(重命名)，false时则直接返回已存在图片的key", paramType = "header", dataTypeClass = String.class),
    })
    public Result<String> upload(UploadedFile file) {
        String upload = AliyunOssUtils.upload(file.getName(), new Media(file.getContent(), file.getContentType(), file.getContentSize()));
        return Result.success("上传成功！", upload);
    }

    @ApiOperation(value = "获取文件url")
    @Mapping(value = "url", method = MethodType.GET)
    public Result<String> getUrl(@Param("key") String key) {
        return Result.success("", AliyunOssUtils.getUrl(key));
    }

    @ApiOperation(value = "预览")
    @Mapping(value = "view", method = MethodType.GET)
    public void view(@Param("key") String key) {
        AliyunOssUtils.view(key);
    }

    @ApiOperation(value = "删除文件")
    @Mapping(value = "delete", method = MethodType.GET)
    public Result<Boolean> delete(@Param("key") String key) {
        if (AliyunOssUtils.delete(key)) {
            return Result.success();
        }
        return Result.error();
    }
}