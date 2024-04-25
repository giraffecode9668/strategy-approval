package com.giraffe.strategy.test;

import com.giraffe.entity.TestApproval;
import com.giraffe.strategy.frame.CustomApprovalBO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TestApprovalCustomApprovalBO extends CustomApprovalBO {

    public TestApprovalCustomApprovalBO(String strategyName, String definitionKey, Long definitionValue, String nodeKey, TestApproval testApproval) {
        super(strategyName, definitionKey, definitionValue, nodeKey);
        this.testApproval = testApproval;
    }

    private TestApproval testApproval;

}
