package com.superb.flowable.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 13:50
 */
@Data
@ApiModel("用户任务")
public class FlowUserTask {

    @ApiModelProperty("任务ID")
    private String id;

    @ApiModelProperty(value = "任务名称", notes = "节点名称")
    private String name;

    @ApiModelProperty("节点key")
    private String key;

    @ApiModelProperty("执行人")
    private String assignee;

    @ApiModelProperty("候选人")
    private List<String> candidateUsers;

    @ApiModelProperty(value = "候选组", notes = "候选部门")
    private List<String> candidateGroups;

    @ApiModelProperty("流程自定义属性")
    private Map<String, Object> flowCustomProps;
}
