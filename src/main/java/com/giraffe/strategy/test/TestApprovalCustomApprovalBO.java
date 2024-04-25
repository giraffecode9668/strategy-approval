package cn.sohan.m2m.order.strategy.cardApproval;

import cn.sohan.m2m.order.basic.model.entity.CardApproval;
import cn.sohan.m2m.pkg.basic.model.entity.PackageApproval;
import cn.sohan.m2m.tool.module.strategy.CustomApprovalBO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CardApprovalCustomApprovalBO extends CustomApprovalBO {

    public CardApprovalCustomApprovalBO(String strategyName, String definitionKey, Long definitionValue, String nodeKey, CardApproval cardApproval) {
        super(strategyName, definitionKey, definitionValue, nodeKey);
        this.cardApproval = cardApproval;
    }

    private CardApproval cardApproval;

    // 中间传值
    // 企微通知Id
    private List<String> qyUserIds;
}
