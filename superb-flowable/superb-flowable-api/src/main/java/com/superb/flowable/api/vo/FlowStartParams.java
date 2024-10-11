package com.superb.flowable.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-22 11:12
 */
@Data
@ApiModel("流程启动参数")
public class FlowStartParams {

    @ApiModelProperty(value = "流程标识", required = true)
    private String flowKey;

    @ApiModelProperty(value = "业务主键", required = true)
    private String businessKey;

    @ApiModelProperty("流程变量")
    private Map<String, Object> variables;

    @ApiModelProperty("是否跳过第一(开始)节点")
    private boolean isSkipFirstNode = false;

    @ApiModelProperty(value = "流程名称", required = true)
    private String processName;

}
