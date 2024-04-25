package com.giraffe.strategy.frame;

import com.giraffe.utils.CommonSpringContextUtil;
import com.giraffe.utils.LoginUser;
import com.giraffe.dao.CustomFlowNodeRepository;
import com.giraffe.entity.CustomFlowNode;
import com.giraffe.enums.CustomFlowNodeStatusEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractApprovalStrategy implements ApprovalStrategy {


    public abstract CustomFlowNode buildCreateNode(CustomApprovalBO bo);
    public abstract void postProcessAfterCreateNode(CustomApprovalBO bo);
    public abstract void postProcessAfterExecuteNode(CustomApprovalBO bo, boolean isPass, String refuseReason, Map<String, Object> variables);
    public abstract void postProcessAfterFinishNode(CustomApprovalBO bo);


    @Override
    public void doCreate(CustomApprovalBO bo) {
        this.createNode(bo);
    }

    @Override
    public void doApprove(CustomApprovalBO bo, boolean isPass, String refuseReason, Map<String, Object> variables) {
        this.executeNode(bo, isPass, refuseReason, variables);
        this.finishNode(bo, isPass, refuseReason);
    }


    void createNode(CustomApprovalBO bo) {
        CustomFlowNodeRepository flowNodeRepository = CommonSpringContextUtil.getBean(CustomFlowNodeRepository.class);

        CustomFlowNode customFlowNode = this.buildCreateNode(bo);
        if (customFlowNode == null) {
            throw new RuntimeException("创建节点失败");
        }
        Integer maxOrder = flowNodeRepository.getMaxOrder(customFlowNode.getDefinitionKey(), customFlowNode.getDefinitionValue());
        customFlowNode.setNodeOrder(maxOrder + 1);
        flowNodeRepository.save(customFlowNode);
        bo.setCustomFlowNode(customFlowNode);

        // 执行创建节点后续操作
        this.postProcessAfterCreateNode(bo);
    }

    /**
     * 执行节点
     */
    public void executeNode(CustomApprovalBO bo, boolean isPass, String refuseReason, Map<String, Object> variables) {
        CustomFlowNodeRepository flowNodeRepository = CommonSpringContextUtil.getBean(CustomFlowNodeRepository.class);
        CustomFlowNode customFlowNode = flowNodeRepository.getTodoFlowNode(bo.getDefinitionKey(), bo.getDefinitionValue(), bo.getNodeKey());
        if (Objects.nonNull(customFlowNode)) {
            bo.setCustomFlowNode(customFlowNode);
            flowNodeRepository.updateStatus(customFlowNode.getId(), 2);
        }

        // 执行节点后续操作
        this.postProcessAfterExecuteNode(bo, isPass, refuseReason, variables);
    }


    /**
     * 完成节点
     */
    public void finishNode(CustomApprovalBO bo, boolean isPass, String refuseReason) {
        CustomFlowNodeRepository flowNodeRepository = CommonSpringContextUtil.getBean(CustomFlowNodeRepository.class);

        CustomFlowNode customFlowNode = bo.getCustomFlowNode();
        if (Objects.nonNull(customFlowNode)) {

            LoginUser loginUser = getLoginUser();

            CustomFlowNode customFlowNodeUpdate = new CustomFlowNode();
            customFlowNodeUpdate.setId(customFlowNode.getId());
            if (!isPass) {
                customFlowNodeUpdate.setNodeStatus(CustomFlowNodeStatusEnum.REJECT.getCode());
                customFlowNodeUpdate.setRefuseReason(refuseReason);
            } else {
                customFlowNodeUpdate.setNodeStatus(CustomFlowNodeStatusEnum.PASS.getCode());
            }
            customFlowNodeUpdate.setNodeCompleteUserId(loginUser.getId());
            customFlowNodeUpdate.setNodeCompleteUserName(loginUser.getRealname());
            customFlowNodeUpdate.setNodeCompleteTime(LocalDateTime.now());
            flowNodeRepository.updateById(customFlowNodeUpdate);
        }

        // 执行节点后续操作
        this.postProcessAfterFinishNode(bo);
    }

    public LoginUser getLoginUser() {
        LoginUser sysUser = new LoginUser();
        sysUser.setAccount("admin");
        sysUser.setId(1L);
        sysUser.setRealname("系统");
        // todo 获取当前登录用户
//        return Optional.ofNullable(SecurityUtil.getLoginUser()).orElse(sysUser);
        return sysUser;
    }

    public void terminateAllUnFinishFlowNode(CustomApprovalBO bo) {
        CustomFlowNodeRepository flowNodeRepository = CommonSpringContextUtil.getBean(CustomFlowNodeRepository.class);
        List<CustomFlowNode> unFinishFlowNodeList = flowNodeRepository.getUnFinishFlowNodeList(bo.getDefinitionKey(), bo.getDefinitionValue());
        unFinishFlowNodeList.forEach(flowNode -> {
            flowNodeRepository.updateStatus(flowNode.getId(), CustomFlowNodeStatusEnum.TERMINATED.getCode());
        });
    }


}
