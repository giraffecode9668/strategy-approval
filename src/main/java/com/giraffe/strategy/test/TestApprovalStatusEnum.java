package com.giraffe.strategy.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestApprovalStatusEnum implements ApprovalStatusInterface {

    NULL(-1, "空状态", null, null, null),
    INIT_SUBMIT(1, "提交申请", "testInitApprovalStrategy", null, null),
    OPERATION_APPROVAL(2, "运营审批", "testOprApprovalStrategy", null, null),
    PASS(10, "通过申请", "", null, null),
    REJECT(11, "驳回申请", "", null, null),
    CANCEL(12, "取消申请", "testCancelApprovalStrategy", null, null),
    ;

    static {
        INIT_SUBMIT.nextStatusY = OPERATION_APPROVAL;

        OPERATION_APPROVAL.nextStatusY = PASS;
        OPERATION_APPROVAL.nextStatusN = REJECT;
    }

    private final Integer code;
    private final String desc;
    private final String strategy;

    private TestApprovalStatusEnum nextStatusY;
    private TestApprovalStatusEnum nextStatusN;

    public static TestApprovalStatusEnum getByCode(Integer code) {
        for (TestApprovalStatusEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException(String.format("TestApprovalStatusEnum not exist code [%s]", code));
    }

    @Override
    public String getNodeKey() {
        return String.valueOf(this.code);
    }
}
