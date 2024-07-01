package com.giraffe.strategy.test;

import com.giraffe.entity.TestApproval;
import com.giraffe.strategy.frame.CustomApprovalBO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TestApprovalCustomApprovalBO extends CustomApprovalBO {

    public TestApprovalCustomApprovalBO(String strategyName, String definitionKey, Long definitionValue, String nodeKey, Object testApproval) {
        super(strategyName, definitionKey, definitionValue, nodeKey, testApproval);
        this.testApproval = (TestApproval) testApproval;
    }

    private TestApproval testApproval;

    public static TestApprovalCustomApprovalBO buildFromParentBO(CustomApprovalBO bo) {
        return new TestApprovalCustomApprovalBO(bo.getStrategyName(), bo.getDefinitionKey(), bo.getDefinitionValue(), bo.getNodeKey(), bo.getCustomData());
    }
}
