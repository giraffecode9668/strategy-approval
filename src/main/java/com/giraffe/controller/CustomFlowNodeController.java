package com.giraffe.controller;


import com.giraffe.service.CustomApproveService;
import com.giraffe.strategy.frame.ProcessStatusEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/customFlowNode")
public class CustomFlowNodeController {

    @Resource
    private CustomApproveService customApproveService;

    @GetMapping("/queryProcessStatus")
    public List<ProcessStatusEntity> queryProcessStatus(@RequestParam String approvalId, @RequestParam String type) {
        return customApproveService.queryProcessStatusByIdAndType(approvalId, type);
    }


}
