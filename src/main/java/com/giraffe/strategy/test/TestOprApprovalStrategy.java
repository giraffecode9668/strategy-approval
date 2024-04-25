package com.giraffe.strategy.test;

import com.giraffe.dao.TestApprovalRepository;
import com.giraffe.entity.CustomFlowNode;
import com.giraffe.entity.TestApproval;
import com.giraffe.enums.CustomFlowNodeStatusEnum;
import com.giraffe.strategy.frame.CustomApprovalBO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component("testOprApprovalStrategy")
public class TestOprApprovalStrategy extends AbstractTestApprovalStrategy {

    @Resource
    private TestApprovalRepository testApprovalRepository;

    @Override
    public CustomFlowNode buildCreateNode(CustomApprovalBO bo) {

        String definitionKey = bo.getDefinitionKey();
        Long definitionValue = bo.getDefinitionValue();
        String nodeKey = bo.getNodeKey();

        CustomFlowNode customFlowNode = new CustomFlowNode();
        customFlowNode.setDefinitionKey(definitionKey);
        customFlowNode.setDefinitionValue(definitionValue);
        customFlowNode.setNodeKey(nodeKey);
        customFlowNode.setNodeName(TestApprovalStatusEnum.OPERATION_APPROVAL.getDesc());
        customFlowNode.setNodeStatus(CustomFlowNodeStatusEnum.NOT_EXECUTED.getCode());

        return customFlowNode;
    }

    @Override
    public void postProcessAfterCreateNode(CustomApprovalBO bo) {
        TestApprovalCustomApprovalBO testCustomApprovalBO = AbstractTestApprovalStrategy.getTestCustomApprovalBO(bo);
        log.info("处理逻辑 更新 testApproval 状态 为 运营审批");

        TestApproval testApproval = testCustomApprovalBO.getTestApproval();
        testApproval.setApprovalStatus(TestApprovalStatusEnum.OPERATION_APPROVAL.getCode());
        testApprovalRepository.updateById(testApproval);

    }

    @Override
    public void postProcessAfterExecuteNode(CustomApprovalBO bo, boolean isPass, String refuseReason, Map<String, Object> variables) {
        // 审批处理逻辑
        TestApprovalCustomApprovalBO testCustomApprovalBO = AbstractTestApprovalStrategy.getTestCustomApprovalBO(bo);
        TestApproval testApproval = testCustomApprovalBO.getTestApproval();

        // 路由 下一个节点 或者结束流程
        if (isPass) {
            testApproval.setApprovalStatus(TestApprovalStatusEnum.PASS.getCode());
            testApprovalRepository.updateById(testApproval);
        } else {
            testApproval.setApprovalStatus(TestApprovalStatusEnum.REJECT.getCode());
            testApprovalRepository.updateById(testApproval);
        }

    }

    @Override
    public void postProcessAfterFinishNode(CustomApprovalBO bo) {

    }
}
