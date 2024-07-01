package com.giraffe.strategy.frame;

import com.giraffe.entity.CustomFlowNode;
import lombok.Data;

@Data
public class CustomApprovalBO {


    // 传参
    private String strategyName;

    private String definitionKey;

    private Long definitionValue;

    private String nodeKey;

    private boolean commonExecutePostProcess = false;

    // 自定义参数
    private Object customData;

    public CustomApprovalBO() {
    }

    public CustomApprovalBO(String strategyName, String definitionKey, Long definitionValue, String nodeKey, Object customData) {
        this.strategyName = strategyName;
        this.definitionKey = definitionKey;
        this.definitionValue = definitionValue;
        this.nodeKey = nodeKey;
        this.customData = customData;
    }

    // 中间变量
    private CustomFlowNode customFlowNode;

}
