package com.giraffe.strategy.frame;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author chenwenyu
 * @description:
 * @menu
 * @date 2022/4/1 16:39
 */
@Data
public class ProcessStatusEntity {
    private String taskName;
    private String assignee;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String approved;
    // @ApiModelProperty("审批人名称")
    private String approvedName;
    private String roleNames;
    private String comment;
    private String taskId;
    private String processInstanceId;
    private String taskDefinitionKey;

}
