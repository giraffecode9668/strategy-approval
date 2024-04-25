package cn.sohan.m2m.order.strategy.cardApproval;

import cn.sohan.m2m.framework.util.SohanSpringContextUtil;
import cn.sohan.m2m.module.commission.service.commission.strategy.AbstractCommissionApprovalStrategy;
import cn.sohan.m2m.order.basic.model.entity.CardApproval;
import cn.sohan.m2m.order.basic.model.enums.CardApprovalStatusEnum;
import cn.sohan.m2m.pkg.module.strategy.packageApproval.AbstractPackageApprovalStrategy;
import cn.sohan.m2m.pkg.module.strategy.packageApproval.PackageApprovalCustomApprovalBO;
import cn.sohan.m2m.tool.module.model.entity.CustomFlowNode;
import cn.sohan.m2m.tool.module.service.CustomApproveService;
import cn.sohan.m2m.tool.module.strategy.CustomApprovalBO;
import com.sohan.enums.CustomFlowNodeStatusEnum;
import com.sohan.enums.PackageApprovalStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component("cardCancelApprovalStrategy")
public class CardCancelApprovalStrategy extends AbstractCommissionApprovalStrategy {


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
        customFlowNode.setNodeName("撤销申请");
        customFlowNode.setNodeStatus(CustomFlowNodeStatusEnum.NOT_EXECUTED.getCode());

        return customFlowNode;
    }

    @Override
    public void postProcessAfterCreateNode(CustomApprovalBO bo) {
        CardApprovalCustomApprovalBO cardCustomApprovalBO = AbstractCardApprovalStrategy.getCardCustomApprovalBO(bo);
        CardApproval cardApproval = cardCustomApprovalBO.getCardApproval();

        // 完成 取消
        CardApprovalCustomApprovalBO nextBO = new CardApprovalCustomApprovalBO(
                CardApprovalStatusEnum.CANCEL.getStrategy(),
                bo.getDefinitionKey(),
                bo.getDefinitionValue(),
                CardApprovalStatusEnum.CANCEL.getCode().toString(),
                cardApproval
        );

        SohanSpringContextUtil.getBean(CustomApproveService.class).doApprove(nextBO, true, null, null);

    }



    @Override
    public void postProcessAfterExecuteNode(CustomApprovalBO bo, boolean isPass, String refuseReason, Map<String, Object> variables) {


    }

    @Override
    public void postProcessAfterFinishNode(CustomApprovalBO bo) {

    }

}
