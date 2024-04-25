package cn.sohan.m2m.order.strategy.cardApproval;

import cn.sohan.m2m.customer.basic.config.CustomerCache;
import cn.sohan.m2m.customer.basic.model.entity.Customer;
import cn.sohan.m2m.framework.util.SohanSpringContextUtil;
import cn.sohan.m2m.order.basic.model.entity.CardApproval;
import cn.sohan.m2m.order.basic.model.entity.CardApprovalCardInfo;
import cn.sohan.m2m.order.basic.model.enums.CardApprovalStatusEnum;
import cn.sohan.m2m.order.service.card.approval.CardApprovalCardInfoService;
import cn.sohan.m2m.order.service.card.approval.CardApprovalService;
import cn.sohan.m2m.tool.module.model.entity.CustomFlowNode;
import cn.sohan.m2m.tool.module.service.appNotify.WxcpApplicationNotifyService;
import cn.sohan.m2m.tool.module.strategy.CustomApprovalBO;
import com.sohan.constants.QyNoticeConstant;
import com.sohan.easy4j.exception.BusinessException;
import com.sohan.enums.CustomFlowNodeStatusEnum;
import com.sohan.util.StringUtils;
import hk.sohan.easy4j.admin.modular.entity.SysUser;
import hk.sohan.easy4j.admin.modular.service.SysUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component("cardOprApprovalStrategy")
public class CardOprApprovalStrategy extends AbstractCardApprovalStrategy {

    @Resource
    private WxcpApplicationNotifyService wxcpApplicationNotifyService;
    @Resource
    private SysUserService sysUserService;
    @Value("${wxcp.msg.cardApproval.url:http://m2m-ts.sohan.cn/#/approval/orderDetail?approvalId=}")
    private String cardApprovalDetailUrl;
    @Resource
    private CustomerCache customerCache;

    @Override
    public CustomFlowNode buildCreateNode(CustomApprovalBO bo) {
        CardApprovalCustomApprovalBO cardCustomApprovalBO = AbstractCardApprovalStrategy.getCardCustomApprovalBO(bo);

        String definitionKey = bo.getDefinitionKey();
        Long definitionValue = bo.getDefinitionValue();
        String nodeKey = bo.getNodeKey();

        CustomFlowNode customFlowNode = new CustomFlowNode();
        customFlowNode.setDefinitionKey(definitionKey);
        customFlowNode.setDefinitionValue(definitionValue);
        customFlowNode.setNodeKey(nodeKey);
        customFlowNode.setNodeName(CardApprovalStatusEnum.OPERATION_APPROVAL.getMsg());
        customFlowNode.setNodeStatus(CustomFlowNodeStatusEnum.NOT_EXECUTED.getCode());

        SysUser yyzj = sysUserService.findYYZJ();
        if (Objects.isNull(yyzj)) {
            throw new BusinessException("运营总监不存在!");
        }

        customFlowNode.setNodeCandidateUserId(yyzj.getId());
        customFlowNode.setNodeCandidateUserName(yyzj.getRealname());

        // 企微通知审批人
        if (StringUtils.isNotBlank(yyzj.getQyUserId())) {
            ArrayList<String> qyUserIds = new ArrayList<>();
            qyUserIds.add(yyzj.getQyUserId());
            cardCustomApprovalBO.setQyUserIds(qyUserIds);
        }

        return customFlowNode;
    }

    @Override
    public void postProcessAfterCreateNode(CustomApprovalBO bo) {
        CardApprovalCustomApprovalBO cardCustomApprovalBO = AbstractCardApprovalStrategy.getCardCustomApprovalBO(bo);
        CardApproval cardApproval = cardCustomApprovalBO.getCardApproval();

        List<String> qyUserIdList = cardCustomApprovalBO.getQyUserIds();

        Long approvalId = cardApproval.getId();
        // 企微通知审批人
        if (CollectionUtils.isNotEmpty(qyUserIdList)) {

            // 发送企微通知
            log.info("[发卡审批] 创建推送企微通知 id[{}]为任务【{}】接收用户【{}】", approvalId, bo.getNodeKey(), qyUserIdList);
            CardApprovalCardInfo cardApprovalCardInfo = SohanSpringContextUtil.getBean(CardApprovalCardInfoService.class).queryByApprovalId(approvalId);

            SysUser sysUser = sysUserService.selectById(cardApproval.getCreateUserId());

            Boolean sendFlag = wxcpApplicationNotifyService.sendNotify(
                    QyNoticeConstant.NoticeTitleEnum.CARD_APPROVAL_WAIT.getTitle(),
                    String.format(
                            QyNoticeConstant.NoticeTitleEnum.CARD_APPROVAL_WAIT.getContent(),
                            Optional.ofNullable(customerCache.getCacheById(cardApproval.getCustomerId())).map(Customer::getCustomerName).orElse("")
                            , Optional.ofNullable(cardApprovalCardInfo).map(CardApprovalCardInfo::getNumber).orElse(0)
                            , Optional.ofNullable(sysUser).map(SysUser::getRealname).orElse("")
                    ),
                    qyUserIdList,
                    cardApprovalDetailUrl + approvalId
            );
            log.info("[发卡审批] 创建推送企微通知 id[{}]为任务【{}】接收用户【{}】发送结果[{}]", approvalId, bo.getNodeKey(), qyUserIdList, sendFlag);

        }

    }

    @Override
    public void postProcessAfterExecuteNode(CustomApprovalBO bo, boolean isPass, String refuseReason, Map<String, Object> variables) {
        CardApprovalCustomApprovalBO cardCustomApprovalBO = AbstractCardApprovalStrategy.getCardCustomApprovalBO(bo);
        CardApproval cardApproval = cardCustomApprovalBO.getCardApproval();

        // 校验权限
        this.checkPermission(cardCustomApprovalBO.getCustomFlowNode());

        // 日志与状态更新
        SohanSpringContextUtil.getBean(CardApprovalService.class).approvalAction(cardApproval.getId(), refuseReason, isPass);

    }

    @Override
    public void postProcessAfterFinishNode(CustomApprovalBO bo) {

    }
}
