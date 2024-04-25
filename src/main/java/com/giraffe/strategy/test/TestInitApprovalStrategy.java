package cn.sohan.m2m.order.strategy.cardApproval;

import cn.sohan.m2m.framework.util.SohanSpringContextUtil;
import cn.sohan.m2m.order.basic.model.enums.CardApprovalStatusEnum;
import cn.sohan.m2m.tool.module.model.entity.CustomFlowNode;
import cn.sohan.m2m.tool.module.service.CustomApproveService;
import cn.sohan.m2m.tool.module.strategy.CustomApprovalBO;
import com.sohan.enums.CustomFlowNodeStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component("cardInitApprovalStrategy")
public class CardInitApprovalStrategy extends AbstractCardApprovalStrategy {


    @Override
    public CustomFlowNode buildCreateNode(CustomApprovalBO bo) {

        String definitionKey = bo.getDefinitionKey();
        Long definitionValue = bo.getDefinitionValue();
        String nodeKey = bo.getNodeKey();


        CustomFlowNode customFlowNode = new CustomFlowNode();
        customFlowNode.setDefinitionKey(definitionKey);
        customFlowNode.setDefinitionValue(definitionValue);
        customFlowNode.setNodeKey(nodeKey);
        customFlowNode.setNodeName(CardApprovalStatusEnum.INIT_SUBMIT.getMsg());
        customFlowNode.setNodeStatus(CustomFlowNodeStatusEnum.NOT_EXECUTED.getCode());

        return customFlowNode;
    }

    @Override
    public void postProcessAfterCreateNode(CustomApprovalBO bo) {
        CardApprovalCustomApprovalBO cardCustomApprovalBO = AbstractCardApprovalStrategy.getCardCustomApprovalBO(bo);

        // 审批通过
        CardApprovalCustomApprovalBO nextBO = new CardApprovalCustomApprovalBO(
                CardApprovalStatusEnum.INIT_SUBMIT.getStrategy(),
                bo.getDefinitionKey(),
                bo.getDefinitionValue(),
                CardApprovalStatusEnum.INIT_SUBMIT.getCode().toString(),
                cardCustomApprovalBO.getCardApproval()
        );
        SohanSpringContextUtil.getBean(CustomApproveService.class).doApprove(nextBO, true, null, null);

    }

    @Override
    public void postProcessAfterExecuteNode(CustomApprovalBO bo, boolean isPass, String refuseReason, Map<String, Object> variables) {
        CardApprovalCustomApprovalBO cardCustomApprovalBO = AbstractCardApprovalStrategy.getCardCustomApprovalBO(bo);

        // 路由下一个节点
        if (isPass) {

            // 审批通过
            CardApprovalCustomApprovalBO nextBO = new CardApprovalCustomApprovalBO(
                    CardApprovalStatusEnum.OPERATION_APPROVAL.getStrategy(),
                    bo.getDefinitionKey(),
                    bo.getDefinitionValue(),
                    CardApprovalStatusEnum.OPERATION_APPROVAL.getCode().toString(),
                    cardCustomApprovalBO.getCardApproval()
            );

            SohanSpringContextUtil.getBean(CustomApproveService.class).doCreate(nextBO);

        }

    }

    @Override
    public void postProcessAfterFinishNode(CustomApprovalBO bo) {

    }
}
