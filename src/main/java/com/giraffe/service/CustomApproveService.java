package cn.sohan.m2m.tool.module.service;

import cn.sohan.m2m.customer.basic.service.ClientBusinessBasicService;
import cn.sohan.m2m.framework.util.SohanSpringContextUtil;
import cn.sohan.m2m.tool.module.dao.CustomFlowNodeRepository;
import cn.sohan.m2m.tool.module.model.entity.CustomFlowNode;
import cn.sohan.m2m.tool.module.model.entity.ProcessStatusEntity;
import cn.sohan.m2m.tool.module.strategy.ApprovalStrategy;
import cn.sohan.m2m.tool.module.strategy.CustomApprovalBO;
import cn.sohan.m2m.tool.module.strategy.CustomFlowNodeCandidateUserVO;
import cn.sohan.m2m.tool.module.strategy.CustomFlowNodeVO;
import com.sohan.constants.WorkFlowConstantValues;
import com.sohan.easy4j.exception.BusinessException;
import com.sohan.enums.CustomFlowNodeStatusEnum;
import com.sohan.util.StringUtils;
import hk.sohan.easy4j.admin.modular.service.SysUserService;
import jakarta.annotation.Resource;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
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

    public List<ProcessStatusEntity> queryProcessStatusByIdAndType(String approvalId, Integer type) {
        List<ProcessStatusEntity> result = new ArrayList<>();
        WorkFlowConstantValues.ProcessDefinitionKeyEnum definitionKeyEnum = WorkFlowConstantValues.ProcessDefinitionKeyEnum.getByCode(type);
        if (Objects.isNull(definitionKeyEnum)) {
            return result;
        }

        List<CustomFlowNode> flowNodeList = customFlowNodeRepository.getFlowNodeList(definitionKeyEnum.getCustomKey(), Long.valueOf(approvalId));
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
            return Lists.newArrayList();
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

            SohanSpringContextUtil.getBean(SysUserService.class).findValidUserListByRoleCode(nodeCandidateRoleCode).forEach(sysUser -> {
                CustomFlowNodeCandidateUserVO customFlowNodeCandidateUserVO = new CustomFlowNodeCandidateUserVO();
                customFlowNodeCandidateUserVO.setUserId(sysUser.getId());
                customFlowNodeCandidateUserVO.setUserName(sysUser.getRealname());
                result.add(customFlowNodeCandidateUserVO);
            });
        } else if (Objects.nonNull(customFlowNode.getNodeCandidateOprCustomerId()) && customFlowNode.getNodeCandidateOprCustomerId() != 0) {
            Long deliveryManagerId = SohanSpringContextUtil.getBean(ClientBusinessBasicService.class).getDeliveryManagerIdByCustomerId(customFlowNode.getNodeCandidateOprCustomerId());

            Optional.ofNullable(SohanSpringContextUtil.getBean(SysUserService.class).getById(deliveryManagerId)).ifPresent(user -> {
                CustomFlowNodeCandidateUserVO customFlowNodeCandidateUserVO = new CustomFlowNodeCandidateUserVO();
                customFlowNodeCandidateUserVO.setUserId(user.getId());
                customFlowNodeCandidateUserVO.setUserName(user.getRealname());
                result.add(customFlowNodeCandidateUserVO);
            });
        }
        return result;
    }

    public void migrateCommissionApprovalNode(List<ProcessStatusEntity> importProcessStatusEntities, String definitionKey, Long definitionValue) {
        for (ProcessStatusEntity importProcessStatusEntity : importProcessStatusEntities) {
            CustomFlowNode customFlowNode = new CustomFlowNode();
            customFlowNode.setDefinitionKey(definitionKey);
            customFlowNode.setDefinitionValue(definitionValue);
            customFlowNode.setNodeName(importProcessStatusEntity.getTaskName());

            if (Objects.equals("运营审批", importProcessStatusEntity.getTaskName())) {
                customFlowNode.setNodeKey("10");
            } else if (Objects.equals("财务BP审批", importProcessStatusEntity.getTaskName()) || Objects.equals("财务审批", importProcessStatusEntity.getTaskName())) {
                customFlowNode.setNodeKey("11");
            } else if (Objects.equals("一级客户审批", importProcessStatusEntity.getTaskName())) {
                customFlowNode.setNodeKey("12");
            } else if (Objects.equals("CFO审批", importProcessStatusEntity.getTaskName())) {
                customFlowNode.setNodeKey("13");
            } else {
                continue;
            }

            Integer nodeStatus = CustomFlowNodeStatusEnum.NOT_EXECUTED.getCode();
            if (Objects.nonNull(importProcessStatusEntity.getEndTime())) {
                if (Objects.equals(importProcessStatusEntity.getApproved(), "Y")) {
                    nodeStatus = CustomFlowNodeStatusEnum.PASS.getCode();
                } else if (Objects.equals(importProcessStatusEntity.getApproved(), "N")) {
                    nodeStatus = CustomFlowNodeStatusEnum.REJECT.getCode();
                }
            }

            customFlowNode.setNodeStatus(nodeStatus);

            if (!Objects.equals(nodeStatus, CustomFlowNodeStatusEnum.NOT_EXECUTED.getCode())) {
                customFlowNode.setNodeCompleteUserId(StringUtils.isNotBlank(importProcessStatusEntity.getAssignee()) ? Long.valueOf(importProcessStatusEntity.getAssignee()) : null);
                customFlowNode.setNodeCompleteUserName(importProcessStatusEntity.getApprovedName());
            } else {
                customFlowNode.setNodeCandidateUserId(StringUtils.isNotBlank(importProcessStatusEntity.getAssignee()) ? Long.valueOf(importProcessStatusEntity.getAssignee()) : null);
                customFlowNode.setNodeCandidateUserName(importProcessStatusEntity.getApprovedName());
            }

            customFlowNode.setRefuseReason(importProcessStatusEntity.getComment());
            customFlowNode.setNodeOrder(customFlowNodeRepository.getMaxOrder(definitionKey, definitionValue) + 1);

            customFlowNode.setNodeCompleteTime(importProcessStatusEntity.getEndTime());
            customFlowNode.setCreateTime(importProcessStatusEntity.getCreateTime());

            customFlowNodeRepository.save(customFlowNode);
        }

    }

    public void migratePackageApprovalNode(List<ProcessStatusEntity> importProcessStatusEntities, String definitionKey, Long definitionValue) {
        for (ProcessStatusEntity importProcessStatusEntity : importProcessStatusEntities) {
            CustomFlowNode customFlowNode = new CustomFlowNode();
            customFlowNode.setDefinitionKey(definitionKey);
            customFlowNode.setDefinitionValue(definitionValue);
            customFlowNode.setNodeName(importProcessStatusEntity.getTaskName());

            if (Objects.equals("运营审批", importProcessStatusEntity.getTaskName())) {
                customFlowNode.setNodeKey("10");
            } else {
                continue;
            }

            Integer nodeStatus = CustomFlowNodeStatusEnum.NOT_EXECUTED.getCode();
            if (Objects.nonNull(importProcessStatusEntity.getEndTime())) {
                if (Objects.equals(importProcessStatusEntity.getApproved(), "Y")) {
                    nodeStatus = CustomFlowNodeStatusEnum.PASS.getCode();
                } else if (Objects.equals(importProcessStatusEntity.getApproved(), "N")) {
                    nodeStatus = CustomFlowNodeStatusEnum.REJECT.getCode();
                }
            }

            customFlowNode.setNodeStatus(nodeStatus);

            if (!Objects.equals(nodeStatus, CustomFlowNodeStatusEnum.NOT_EXECUTED.getCode())) {
                customFlowNode.setNodeCompleteUserId(StringUtils.isNotBlank(importProcessStatusEntity.getAssignee()) ? Long.valueOf(importProcessStatusEntity.getAssignee()) : null);
                customFlowNode.setNodeCompleteUserName(importProcessStatusEntity.getApprovedName());
            } else {
                customFlowNode.setNodeCandidateUserId(StringUtils.isNotBlank(importProcessStatusEntity.getAssignee()) ? Long.valueOf(importProcessStatusEntity.getAssignee()) : null);
                customFlowNode.setNodeCandidateUserName(importProcessStatusEntity.getApprovedName());
            }

            customFlowNode.setRefuseReason(importProcessStatusEntity.getComment());
            customFlowNode.setNodeOrder(customFlowNodeRepository.getMaxOrder(definitionKey, definitionValue) + 1);

            customFlowNode.setNodeCompleteTime(importProcessStatusEntity.getEndTime());
            customFlowNode.setCreateTime(importProcessStatusEntity.getCreateTime());

            customFlowNodeRepository.save(customFlowNode);
        }

    }

    public void migrateCardApprovalNode(List<ProcessStatusEntity> importProcessStatusEntities, String definitionKey, Long definitionValue) {
        for (ProcessStatusEntity importProcessStatusEntity : importProcessStatusEntities) {
            CustomFlowNode customFlowNode = new CustomFlowNode();
            customFlowNode.setDefinitionKey(definitionKey);
            customFlowNode.setDefinitionValue(definitionValue);
            customFlowNode.setNodeName(importProcessStatusEntity.getTaskName());

            if (Objects.equals("运营审批", importProcessStatusEntity.getTaskName())) {
                customFlowNode.setNodeKey("1");
            } else {
                continue;
            }

            Integer nodeStatus = CustomFlowNodeStatusEnum.NOT_EXECUTED.getCode();
            if (Objects.nonNull(importProcessStatusEntity.getEndTime())) {
                if (Objects.equals(importProcessStatusEntity.getApproved(), "Y")) {
                    nodeStatus = CustomFlowNodeStatusEnum.PASS.getCode();
                } else if (Objects.equals(importProcessStatusEntity.getApproved(), "N")) {
                    nodeStatus = CustomFlowNodeStatusEnum.REJECT.getCode();
                }
            }

            customFlowNode.setNodeStatus(nodeStatus);

            if (!Objects.equals(nodeStatus, CustomFlowNodeStatusEnum.NOT_EXECUTED.getCode())) {
                customFlowNode.setNodeCompleteUserId(StringUtils.isNotBlank(importProcessStatusEntity.getAssignee()) ? Long.valueOf(importProcessStatusEntity.getAssignee()) : null);
                customFlowNode.setNodeCompleteUserName(importProcessStatusEntity.getApprovedName());
            } else {
                customFlowNode.setNodeCandidateUserId(StringUtils.isNotBlank(importProcessStatusEntity.getAssignee()) ? Long.valueOf(importProcessStatusEntity.getAssignee()) : null);
                customFlowNode.setNodeCandidateUserName(importProcessStatusEntity.getApprovedName());
            }

            customFlowNode.setRefuseReason(importProcessStatusEntity.getComment());
            customFlowNode.setNodeOrder(customFlowNodeRepository.getMaxOrder(definitionKey, definitionValue) + 1);

            customFlowNode.setNodeCompleteTime(importProcessStatusEntity.getEndTime());
            customFlowNode.setCreateTime(importProcessStatusEntity.getCreateTime());

            customFlowNodeRepository.save(customFlowNode);
        }

    }
}
