package com.superb.common.database.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotNull;
import org.noear.solon.validation.annotation.Size;

import java.util.List;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-23 13:57
 */
@Data
@ApiModel("批量id 操作")
public class BatchId {

    @NotNull(message = "批量操作内容不能为空")
    @Size(min = 1, message = "批量操作时，至少传递一个操作数据")
    @ApiModelProperty(value = "id集合", notes = "批量操作时传递")
    private List<String> id;

}
