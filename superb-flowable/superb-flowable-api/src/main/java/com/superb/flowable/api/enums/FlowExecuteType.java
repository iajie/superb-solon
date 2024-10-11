package com.superb.flowable.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: ajie
 * @CreateTime: 2024-08-21 15:17
 * 流程执行类型
 */
@AllArgsConstructor
public enum FlowExecuteType {

    START("start", "启动流程"),
    RESUBMIT("resubmit", "重新提交"),
    REVOCATION("revocation", "撤回申请"),
    GATEWAY("gateway", "流程网关流转"),
    AGREE("agree", "审核通过"),
    REJECT("reject", "驳回"),
    REJECT_TO_TASK("rejectToTask", "驳回到指定任务节点"),
    CANCELLATION("cancellation", "流程作废");
    @Getter
    private final String code;
    @Getter
    private final String info;

    /**
     * 根据code获取去INfo
     *
     * @param code
     * @return
     */
    public static String of(String code) {
        for (FlowExecuteType executeType : FlowExecuteType.values()) {
            if (code.equals(executeType.getCode())) {
                return executeType.getInfo();
            }
        }
        return null;
    }
}
