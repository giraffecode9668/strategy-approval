package com.giraffe.strategy;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomFlowNodeVO {

    private Long id;

    private String nodeName;

    private Integer nodeStatus;

    private Integer nodeOrder;

    private String nodeCandidateRoleName;

    private Long nodeCompleteUserId;

    private String nodeCompleteUserName;

    private LocalDateTime nodeCompleteTime;

    private List<CustomFlowNodeCandidateUserVO> nodeCandidateUserList;

}
