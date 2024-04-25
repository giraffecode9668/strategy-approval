package com.giraffe.strategy;

import com.giraffe.entity.CustomFlowNode;
import lombok.Data;

@Data
public class CustomApprovalBO {


    // 传参
    private String strategyName;

    private String definitionKey;

    private Long definitionValue;

    private String nodeKey;

    public CustomApprovalBO() {
    }

    public CustomApprovalBO(String strategyName, String definitionKey, Long definitionValue, String nodeKey) {
        this.strategyName = strategyName;
        this.definitionKey = definitionKey;
        this.definitionValue = definitionValue;
        this.nodeKey = nodeKey;
    }

    // 中间变量
    private CustomFlowNode customFlowNode;

}
