package com.superb.flowable.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 15:26
 */
@AllArgsConstructor
public enum FlowCommentType {

    NORMAL("1", "正常意见"),
    APPROVE("2", "审批意见"),
    REBUT("3", "退回意见"),
    REJECT("4", "驳回意见"),
    REVOCATION("5", "撤回意见"),
    DELEGATE("6", "委派意见"),
    ASSIGN("7", "转办意见"),
    STOP("8", "终止流程"),
    CANCELLATION("9", "作废原因");

    @Getter
    private final String code;
    @Getter
    private final String info;

}
