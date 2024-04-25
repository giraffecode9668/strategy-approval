package com.giraffe.service;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.giraffe.dao.CustomFlowNodeRepository;
import com.giraffe.entity.CustomFlowNode;
import com.giraffe.enums.CustomFlowNodeStatusEnum;
import com.giraffe.strategy.frame.*;
import com.giraffe.utils.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class CustomApproveService {

    @Resource
    private CustomFlowNodeRepository customFlowNodeRepository;


    private final Map<String, ApprovalStrategy> strategyMap;

    public CustomApproveService(Map<String, ApprovalStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    @Transactional
    public void doCreate(CustomApprovalBO bo) {
        ApprovalStrategy strategy = strategyMap.get(bo.getStrategyName());
        if (Objects.isNull(strategy)) {
            throw new BusinessException("未找到对应的审批策略");
        }
        strategy.doCreate(bo);
    }

    @Transactional
    public void doApprove(CustomApprovalBO bo, boolean isPass, String refuseReason, Map<String, Object> variables) {
        ApprovalStrategy strategy = strategyMap.get(bo.getStrategyName());
        if (Objects.isNull(strategy)) {
            throw new BusinessException("未找到对应的审批策略");
        }
        strategy.doApprove(bo, isPass, refuseReason, variables);
    }

    public CustomFlowNodeVO getTodoFlowNodeVO(CustomApprovalBO bo) {
        ApprovalStrategy strategy = strategyMap.get(bo.getStrategyName());
        if (Objects.isNull(strategy)) {
            throw new BusinessException("未找到对应的审批策略");
        }
        return strategy.getTodoFlowNodeVO(bo);
    }

    public List<ProcessStatusEntity> queryProcessStatusByIdAndType(String approvalId, String type) {
        List<ProcessStatusEntity> result = new ArrayList<>();

        List<CustomFlowNode> flowNodeList = customFlowNodeRepository.getFlowNodeList(type, Long.valueOf(approvalId));
        for (CustomFlowNode node : flowNodeList) {
            ProcessStatusEntity processStatusEntity = new ProcessStatusEntity();
            processStatusEntity.setTaskName(node.getNodeName());
            processStatusEntity.setCreateTime(node.getCreateTime());

            if (StringUtils.isNotBlank(node.getNodeCompleteUserName())) {
                processStatusEntity.setApprovedName(node.getNodeCompleteUserName());
            }

            if (Objects.equals(node.getNodeStatus(), CustomFlowNodeStatusEnum.PASS.getCode())) {
                processStatusEntity.setApproved("Y");
                processStatusEntity.setEndTime(node.getNodeCompleteTime());
            } else if (Objects.equals(node.getNodeStatus(), CustomFlowNodeStatusEnum.REJECT.getCode())) {
                processStatusEntity.setApproved("N");
                processStatusEntity.setComment(node.getRefuseReason());
                processStatusEntity.setEndTime(node.getNodeCompleteTime());
            } else if (Objects.equals(node.getNodeStatus(), CustomFlowNodeStatusEnum.TERMINATED.getCode())) {
                processStatusEntity.setApproved("T");
                processStatusEntity.setEndTime(node.getUpdateTime());
            }

            if (StringUtils.isBlank(processStatusEntity.getApprovedName())) {
                List<CustomFlowNodeCandidateUserVO> flowNodeCandidateUserList = this.getFlowNodeCandidateUserList(node, processStatusEntity::setRoleNames);
                if (!CollectionUtils.isEmpty(flowNodeCandidateUserList)) {
                    String collect = flowNodeCandidateUserList.stream().map(CustomFlowNodeCandidateUserVO::getUserName).collect(Collectors.joining(" "));
                    processStatusEntity.setAssignee(collect);
                }
            }


            result.add(processStatusEntity);
        }

        return result;
    }

    private List<CustomFlowNodeCandidateUserVO> getFlowNodeCandidateUserList(CustomFlowNode customFlowNode, Consumer<String> roleCodeConsumer) {
        if (Objects.isNull(customFlowNode)) {
            return new ArrayList<>();
        }
        List<CustomFlowNodeCandidateUserVO> result = new ArrayList<>();
        if (Objects.nonNull(customFlowNode.getNodeCandidateUserId()) && customFlowNode.getNodeCandidateUserId() != 0) {
            CustomFlowNodeCandidateUserVO customFlowNodeCandidateUserVO = new CustomFlowNodeCandidateUserVO();
            customFlowNodeCandidateUserVO.setUserId(customFlowNode.getNodeCandidateUserId());
            customFlowNodeCandidateUserVO.setUserName(customFlowNode.getNodeCandidateUserName());
            result.add(customFlowNodeCandidateUserVO);
        } else if (StringUtils.isNotBlank(customFlowNode.getNodeCandidateRoleCode())) {
            String nodeCandidateRoleCode = customFlowNode.getNodeCandidateRoleCode();
            String nodeCandidateRoleName = customFlowNode.getNodeCandidateRoleName();
            // 角色编码消费者
            roleCodeConsumer.accept(nodeCandidateRoleName);

            // 用户查询
//            CommonSpringContextUtil.getBean(SysUserService.class).findValidUserListByRoleCode(nodeCandidateRoleCode).forEach(sysUser -> {
//                CustomFlowNodeCandidateUserVO customFlowNodeCandidateUserVO = new CustomFlowNodeCandidateUserVO();
//                customFlowNodeCandidateUserVO.setUserId(sysUser.getId());
//                customFlowNodeCandidateUserVO.setUserName(sysUser.getRealname());
//                result.add(customFlowNodeCandidateUserVO);
//            });
        }
        return result;
    }

}
