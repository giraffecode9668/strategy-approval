package com.giraffe.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.giraffe.entity.CustomFlowNode;
import com.giraffe.enums.CustomFlowNodeStatusEnum;
import com.giraffe.mapper.CustomFlowNodeMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomFlowNodeRepository extends ServiceImpl<CustomFlowNodeMapper, CustomFlowNode> {

    public CustomFlowNode getTodoFlowNode(String definitionKey, Long definitionValue, String nodeKey) {
        return this.lambdaQuery()
                .eq(CustomFlowNode::getDefinitionKey, definitionKey)
                .eq(CustomFlowNode::getDefinitionValue, definitionValue)
                .eq(CustomFlowNode::getNodeKey, nodeKey)
                .eq(CustomFlowNode::getNodeStatus, CustomFlowNodeStatusEnum.NOT_EXECUTED.getCode())
                .orderByDesc(CustomFlowNode::getNodeOrder)
                .last("limit 1")
                .one();
    }

    public List<CustomFlowNode> getUnFinishFlowNodeList(String definitionKey, Long definitionValue) {
        return this.lambdaQuery()
                .eq(CustomFlowNode::getDefinitionKey, definitionKey)
                .eq(CustomFlowNode::getDefinitionValue, definitionValue)
                .eq(CustomFlowNode::getNodeStatus, CustomFlowNodeStatusEnum.NOT_EXECUTED.getCode())
                .orderByAsc(CustomFlowNode::getNodeOrder)
                .list();
    }

    public Integer getMaxOrder(String definitionKey, Long definitionValue) {
        CustomFlowNode flowNode = this.lambdaQuery().eq(CustomFlowNode::getDefinitionKey, definitionKey).eq(CustomFlowNode::getDefinitionValue, definitionValue).orderByDesc(CustomFlowNode::getNodeOrder).last("limit 1").one();
        return Optional.ofNullable(flowNode).map(CustomFlowNode::getNodeOrder).orElse(0);
    }

    public void updateStatus(Long id, Integer status) {
        CustomFlowNode customFlowNode = new CustomFlowNode();
        customFlowNode.setId(id);
        customFlowNode.setNodeStatus(status);
        this.updateById(customFlowNode);
    }

    public List<CustomFlowNode> getFlowNodeList(String customKey, Long approvalNo) {
        return this.lambdaQuery().eq(CustomFlowNode::getDefinitionKey, customKey).eq(CustomFlowNode::getDefinitionValue, approvalNo).orderByAsc(CustomFlowNode::getNodeOrder).list();
    }

    public boolean isExistNode(String definitionKey, Long definitionValue) {
        return this.lambdaQuery().eq(CustomFlowNode::getDefinitionKey, definitionKey).eq(CustomFlowNode::getDefinitionValue, definitionValue).count() > 0;
    }
}
