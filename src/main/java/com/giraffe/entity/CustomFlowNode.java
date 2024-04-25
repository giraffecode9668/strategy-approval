package com.giraffe.strategyapproval.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CustomFlowNode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建时间
     */
    @TableId(type = IdType.AUTO)
    private Long id;


    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记：0-未删除; 1-已删除
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 流程定义Key
     */
    private String definitionKey;

    /**
     * 流程定义值
     */
    private Long definitionValue;

    /**
     * 流程节点key
     */
    private String nodeKey;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点状态 1-未执行 2-执行中 3-通过 4-驳回 5-终止
     */
    private Integer nodeStatus;

    /**
     * 驳回原因
     */
    private String refuseReason;


    /**
     * 节点顺序
     */
    private Integer nodeOrder;

    /**
     * 节点完成人ID
     */
    private Long nodeCompleteUserId;

    /**
     * 节点完成人名称
     */
    private String nodeCompleteUserName;

    /**
     * 节点完成时间
     */
    private LocalDateTime nodeCompleteTime;

    /**
     * 节点候选人ID
     */
    private Long nodeCandidateUserId;

    /**
     * 节点候选人名称
     */
    private String nodeCandidateUserName;

    /**
     * 节点候选角色ID
     */
    private String nodeCandidateRoleCode;

    /**
     * 节点候选角色名称
     */
    private String nodeCandidateRoleName;

    /**
     * 节点候选人交付经理 客户ID
     */
    private Long nodeCandidateOprCustomerId;

    /**
     * 节点候选人指定客户
     */
    private Long nodeCandidateCustomerId;

    public CustomFlowNode() {}
}