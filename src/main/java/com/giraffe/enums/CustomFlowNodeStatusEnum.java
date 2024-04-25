package com.giraffe.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomFlowNodeStatusEnum {

    // 节点状态 1-未执行 2-执行中 3-已执行
    NOT_EXECUTED(1, "未执行"),
    EXECUTING(2, "执行中"),
    PASS(3, "已执行"),
    REJECT(4, "已驳回"),
    TERMINATED(5, "已终止")
    ;

    private final Integer code;
    private final String desc;

    public static CustomFlowNodeStatusEnum getByCode(Integer code) {
        for (CustomFlowNodeStatusEnum value : CustomFlowNodeStatusEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
