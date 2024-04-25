package com.giraffe.strategy.test;


import com.giraffe.dao.TestApprovalRepository;
import com.giraffe.entity.CustomFlowNode;
import com.giraffe.entity.TestApproval;
import com.giraffe.enums.CustomFlowNodeStatusEnum;
import com.giraffe.service.CustomApproveService;
import com.giraffe.strategy.frame.CustomApprovalBO;
import com.giraffe.utils.CommonSpringContextUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component("testCancelApprovalStrategy")
public class TestCancelApprovalStrategy extends AbstractTestApprovalStrategy {

    @Resource
    private TestApprovalRepository testApprovalRepository;


    @Override
    public CustomFlowNode buildCreateNode(CustomApprovalBO bo) {
        // 未完结节点 状态更新为 终止
        super.terminateAllUnFinishFlowNode(bo);

        String definitionKey = bo.getDefinitionKey();
        Long definitionValue = bo.getDefinitionValue();
        String nodeKey = bo.getNodeKey();


        CustomFlowNode customFlowNode = new CustomFlowNode();
        customFlowNode.setDefinitionKey(definitionKey);
        customFlowNode.setDefinitionValue(definitionValue);
        customFlowNode.setNodeKey(nodeKey);
        customFlowNode.setNodeName(TestApprovalStatusEnum.CANCEL.getDesc());
        customFlowNode.setNodeStatus(CustomFlowNodeStatusEnum.NOT_EXECUTED.getCode());

        return customFlowNode;
    }

    @Override
    public void postProcessAfterCreateNode(CustomApprovalBO bo) {
        TestApprovalCustomApprovalBO cardCustomApprovalBO = AbstractTestApprovalStrategy.getTestCustomApprovalBO(bo);
        TestApproval testApproval = cardCustomApprovalBO.getTestApproval();

        // 完成 取消
        TestApprovalCustomApprovalBO nextBO = new TestApprovalCustomApprovalBO(
                TestApprovalStatusEnum.CANCEL.getStrategy(),
                bo.getDefinitionKey(),
                bo.getDefinitionValue(),
                TestApprovalStatusEnum.CANCEL.getCode().toString(),
                testApproval
        );

        CommonSpringContextUtil.getBean(CustomApproveService.class).doApprove(nextBO, true, null, null);

    }



    @Override
    public void postProcessAfterExecuteNode(CustomApprovalBO bo, boolean isPass, String refuseReason, Map<String, Object> variables) {

        TestApprovalCustomApprovalBO testCustomApprovalBO = AbstractTestApprovalStrategy.getTestCustomApprovalBO(bo);
        TestApproval testApproval = testCustomApprovalBO.getTestApproval();

        testApproval.setApprovalStatus(TestApprovalStatusEnum.CANCEL.getCode());
        testApprovalRepository.updateById(testApproval);

    }

    @Override
    public void postProcessAfterFinishNode(CustomApprovalBO bo) {

    }

}
