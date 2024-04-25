package com.giraffe.strategy.test;

import com.giraffe.entity.CustomFlowNode;
import com.giraffe.enums.CustomFlowNodeStatusEnum;
import com.giraffe.service.CustomApproveService;
import com.giraffe.strategy.frame.CustomApprovalBO;
import com.giraffe.utils.CommonSpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component("testInitApprovalStrategy")
public class TestInitApprovalStrategy extends AbstractTestApprovalStrategy {


    @Override
    public CustomFlowNode buildCreateNode(CustomApprovalBO bo) {

        String definitionKey = bo.getDefinitionKey();
        Long definitionValue = bo.getDefinitionValue();
        String nodeKey = bo.getNodeKey();


        CustomFlowNode customFlowNode = new CustomFlowNode();
        customFlowNode.setDefinitionKey(definitionKey);
        customFlowNode.setDefinitionValue(definitionValue);
        customFlowNode.setNodeKey(nodeKey);
        customFlowNode.setNodeName(TestApprovalStatusEnum.INIT_SUBMIT.getDesc());
        customFlowNode.setNodeStatus(CustomFlowNodeStatusEnum.NOT_EXECUTED.getCode());

        return customFlowNode;
    }

    @Override
    public void postProcessAfterCreateNode(CustomApprovalBO bo) {
        TestApprovalCustomApprovalBO testCustomApprovalBO = AbstractTestApprovalStrategy.getTestCustomApprovalBO(bo);

        // 审批通过
        TestApprovalCustomApprovalBO nextBO = new TestApprovalCustomApprovalBO(
                TestApprovalStatusEnum.INIT_SUBMIT.getStrategy(),
                bo.getDefinitionKey(),
                bo.getDefinitionValue(),
                TestApprovalStatusEnum.INIT_SUBMIT.getCode().toString(),
                testCustomApprovalBO.getTestApproval()
        );
        CommonSpringContextUtil.getBean(CustomApproveService.class).doApprove(nextBO, true, null, null);

    }

    @Override
    public void postProcessAfterExecuteNode(CustomApprovalBO bo, boolean isPass, String refuseReason, Map<String, Object> variables) {
        TestApprovalCustomApprovalBO testCustomApprovalBO = AbstractTestApprovalStrategy.getTestCustomApprovalBO(bo);

        // 路由下一个节点 自动流转到 运营审批
        if (isPass) {

            TestApprovalCustomApprovalBO nextBO = new TestApprovalCustomApprovalBO(
                    TestApprovalStatusEnum.OPERATION_APPROVAL.getStrategy(),
                    bo.getDefinitionKey(),
                    bo.getDefinitionValue(),
                    TestApprovalStatusEnum.OPERATION_APPROVAL.getCode().toString(),
                    testCustomApprovalBO.getTestApproval()
            );

            CommonSpringContextUtil.getBean(CustomApproveService.class).doCreate(nextBO);

        }

    }

    @Override
    public void postProcessAfterFinishNode(CustomApprovalBO bo) {

    }
}
