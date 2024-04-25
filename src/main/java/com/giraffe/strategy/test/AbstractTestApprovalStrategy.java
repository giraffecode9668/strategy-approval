package cn.sohan.m2m.order.strategy.cardApproval;

import cn.sohan.m2m.tool.module.strategy.AbstractApprovalStrategy;
import cn.sohan.m2m.tool.module.strategy.CustomApprovalBO;
import com.sohan.constants.WorkFlowConstantValues;
import com.sohan.easy4j.exception.BusinessException;

public abstract class AbstractCardApprovalStrategy extends AbstractApprovalStrategy {

    public static final String CARD_APPROVAL_KEY = WorkFlowConstantValues.ProcessDefinitionKeyEnum.NEW_CARD_APPROVAL.getCustomKey();

    public static CardApprovalCustomApprovalBO getCardCustomApprovalBO(CustomApprovalBO bo) {
        if (bo instanceof CardApprovalCustomApprovalBO) {
            return (CardApprovalCustomApprovalBO) bo;
        }
        throw new BusinessException("卡片审批参数类型错误");
    }
}
