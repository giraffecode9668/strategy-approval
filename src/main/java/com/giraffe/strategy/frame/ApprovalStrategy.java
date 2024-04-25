package com.giraffe.strategy.frame;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.giraffe.dao.CustomFlowNodeRepository;
import com.giraffe.entity.CustomFlowNode;
import com.giraffe.utils.BusinessException;
import com.giraffe.utils.CommonSpringContextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface ApprovalStrategy {


    void doCreate(CustomApprovalBO bo);

    void doApprove(CustomApprovalBO bo, boolean isPass, String refuseReason, Map<String, Object> variables);

    default CustomFlowNodeVO getTodoFlowNodeVO(CustomApprovalBO bo) {
        CustomFlowNodeRepository flowNodeRepository = CommonSpringContextUtil.getBean(CustomFlowNodeRepository.class);
        CustomFlowNode customFlowNode = flowNodeRepository.getTodoFlowNode(bo.getDefinitionKey(), bo.getDefinitionValue(), bo.getNodeKey());

        if (Objects.isNull(customFlowNode)) {
            return null;
        }
        CustomFlowNodeVO customFlowNodeVO = new CustomFlowNodeVO();
        customFlowNodeVO.setId(customFlowNode.getId());
        customFlowNodeVO.setNodeName(customFlowNode.getNodeName());
        customFlowNodeVO.setNodeOrder(customFlowNode.getNodeOrder());
        customFlowNodeVO.setNodeStatus(customFlowNode.getNodeStatus());
        customFlowNodeVO.setNodeCompleteTime(customFlowNode.getNodeCompleteTime());
        customFlowNodeVO.setNodeCompleteUserId(customFlowNode.getNodeCompleteUserId());
        customFlowNodeVO.setNodeCompleteUserName(customFlowNode.getNodeCompleteUserName());
        customFlowNodeVO.setNodeCandidateRoleName(customFlowNode.getNodeCandidateRoleName());
        customFlowNodeVO.setNodeCandidateUserList(this.getFlowNodeCandidateUserList(customFlowNode));
        return customFlowNodeVO;
    }

    default List<CustomFlowNodeCandidateUserVO> getFlowNodeCandidateUserList(CustomFlowNode customFlowNode) {
        if (Objects.isNull(customFlowNode)) {
            return new ArrayList<>();
        }
        List<CustomFlowNodeCandidateUserVO> result = new ArrayList<>();
        if (Objects.nonNull(customFlowNode.getNodeCandidateUserId()) && customFlowNode.getNodeCandidateUserId() != 0) {
            CustomFlowNodeCandidateUserVO customFlowNodeCandidateUserVO = new CustomFlowNodeCandidateUserVO();
            customFlowNodeCandidateUserVO.setUserId(customFlowNode.getNodeCandidateUserId());
            customFlowNodeCandidateUserVO.setUserName(customFlowNode.getNodeCandidateUserName());
            result.add(customFlowNodeCandidateUserVO);
        }
        if (StringUtils.isNotBlank(customFlowNode.getNodeCandidateRoleCode())) {
            String nodeCandidateRoleCode = customFlowNode.getNodeCandidateRoleCode();

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

    default void checkPermission(CustomFlowNode customFlowNode) {
        // 校验权限
        List<CustomFlowNodeCandidateUserVO> flowNodeCandidateUserList = this.getFlowNodeCandidateUserList(customFlowNode);
        if (CollectionUtils.isNotEmpty(flowNodeCandidateUserList)) {
            boolean hasPermission = false;
            for (CustomFlowNodeCandidateUserVO userVO : flowNodeCandidateUserList) {
                // 判断是否有权限

                hasPermission = true;
                return;

//                if (Objects.equals(userVO.getUserId(), SecurityUtil.getLoginUserId())) {
//                    hasPermission = true;
//                    break;
//                }
            }
            if (!hasPermission) {
                String candidateUserNameStr = flowNodeCandidateUserList.stream().map(CustomFlowNodeCandidateUserVO::getUserName).collect(Collectors.joining(","));
                throw new BusinessException(String.format("没有权限操作：[%s]的待审批人为[%s]", customFlowNode.getNodeName(), candidateUserNameStr));
            }
        }
    }

}
