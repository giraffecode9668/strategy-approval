package com.giraffe.strategy.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestApprovalStatusEnum {

    INIT_SUBMIT(1, "提交申请", "testInitApprovalStrategy"),
    OPERATION_APPROVAL(2, "运营审批", "testOprApprovalStrategy"),
    PASS(10, "通过申请", ""),
    REJECT(11, "驳回申请", ""),
    CANCEL(12, "取消申请", "testCancelApprovalStrategy"),


    ;

    private final Integer code;
    private final String desc;
    private final String strategy;

    public static TestApprovalStatusEnum getByCode(Integer code) {
        for (TestApprovalStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException(String.format("TestApprovalStatusEnum not exist code [%s]", code));
    }
}
