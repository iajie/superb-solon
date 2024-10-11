package com.superb.common.database.vo;

import com.mybatisflex.core.paginate.Page;
import com.superb.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: ajie
 * @CreateTime: 2024-05-07 16:29
 */
@Data
@ApiModel("分页查询")
public class PageParams<T> {

    @ApiModelProperty(value = "页码")
    private Integer current;

    @ApiModelProperty(value = "页大小")
    private Integer size;

    @ApiModelProperty(value = "查询参数")
    private T params;

    @ApiModelProperty(hidden = true)
    public Page<T> getPage() {
        if (StringUtils.isBlank(current)) {
            current = 1;
        }
        if (StringUtils.isBlank(size)) {
            size = 10;
        }
        return new Page<>(current, size);
    }

}
