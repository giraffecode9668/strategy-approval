package com.giraffe.strategy.test;

import com.giraffe.entity.TestApproval;
import com.giraffe.strategy.frame.AbstractApprovalStrategy;
import com.giraffe.strategy.frame.CustomApprovalBO;
import com.giraffe.utils.BusinessException;

import java.util.Objects;

public abstract class AbstractTestApprovalStrategy extends AbstractApprovalStrategy {

    public static final String TEST_APPROVAL_KEY = "testApproval";

    public <T extends CustomApprovalBO> T getTargetCustomApprovalBO(CustomApprovalBO bo) {
        return (T) getTestCustomApprovalBO(bo);
    }

    public static TestApprovalCustomApprovalBO getTestCustomApprovalBO(CustomApprovalBO bo) {
        if (bo instanceof TestApprovalCustomApprovalBO) {
            return (TestApprovalCustomApprovalBO) bo;
        }
        if (Objects.equals(bo.getDefinitionKey(), TEST_APPROVAL_KEY)) {
            return TestApprovalCustomApprovalBO.buildFromParentBO(bo);
        }
        throw new BusinessException("测试审批参数类型错误");
    }
}
