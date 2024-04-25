create database if not exists strategy_approval;

use strategy_approval;

CREATE TABLE `custom_flow_node` (
                                    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                                    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `is_deleted` int DEFAULT '0' COMMENT '逻辑删除标记：0-未删除; 1-已删除',
                                    `definition_key` varchar(255) NOT NULL DEFAULT '' COMMENT '定义审批key',
                                    `definition_value` bigint unsigned NOT NULL DEFAULT '0' COMMENT '定义审批value 审批主键ID',
                                    `node_key` varchar(255) NOT NULL DEFAULT '' COMMENT '节点key',
                                    `node_name` varchar(255) NOT NULL DEFAULT '' COMMENT '节点名称',
                                    `node_status` tinyint NOT NULL DEFAULT '0' COMMENT '节点状态 1-未执行 2-执行中 3-通过 4-驳回 5-终止',
                                    `refuse_reason` varchar(255) NOT NULL DEFAULT '' COMMENT '节点驳回原因',
                                    `node_order` int NOT NULL DEFAULT '0' COMMENT '节点顺序',
                                    `node_complete_user_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '节点完成人ID',
                                    `node_complete_user_name` varchar(255) NOT NULL DEFAULT '' COMMENT '节点完成人名称',
                                    `node_complete_time` datetime DEFAULT NULL COMMENT '节点完成时间',
                                    `node_candidate_user_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '节点候选人ID',
                                    `node_candidate_user_name` varchar(255) NOT NULL DEFAULT '' COMMENT '节点候选人名称',
                                    `node_candidate_role_code` varchar(255) NOT NULL DEFAULT '' COMMENT '节点候选角色ID',
                                    `node_candidate_role_name` varchar(255) NOT NULL DEFAULT '' COMMENT '节点候选角色名称',
                                    `node_candidate_opr_customer_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '节点候选人交付经理 客户ID',
                                    `node_candidate_customer_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '节点候选人指定客户',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_node_key` (`definition_value`,`node_key`),
                                    KEY `idx_node_candidate_user_id` (`node_candidate_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='自定义审批流节点';

create table test_approval
(
    id                             bigint unsigned auto_increment
        primary key,
    create_time                    datetime        default CURRENT_TIMESTAMP null comment '创建时间',
    update_time                    datetime        default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted                     int             default 0                 null comment '逻辑删除标记：0-未删除; 1-已删除',
    approval_status               int             default 0                 null comment '审批状态：0-未知; 1-提交申请; 2-运营审批; 10-已通过 11-已驳回 12-已撤回'
)
    comment '测试审批';

