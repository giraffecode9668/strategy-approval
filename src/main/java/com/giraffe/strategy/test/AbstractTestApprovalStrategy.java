package com.giraffe.strategy.test;

import com.giraffe.strategy.frame.AbstractApprovalStrategy;
import com.giraffe.strategy.frame.CustomApprovalBO;
import com.giraffe.utils.BusinessException;

public abstract class AbstractTestApprovalStrategy extends AbstractApprovalStrategy {

    public static final String TEST_APPROVAL_KEY = "testApproval";

    public static TestApprovalCustomApprovalBO getTestCustomApprovalBO(CustomApprovalBO bo) {
        if (bo instanceof TestApprovalCustomApprovalBO) {
            return (TestApprovalCustomApprovalBO) bo;
        }
        throw new BusinessException("测试审批参数类型错误");
    }
}
