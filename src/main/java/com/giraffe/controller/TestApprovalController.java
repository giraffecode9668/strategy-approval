package com.giraffe.controller;

import com.giraffe.service.TestApprovalService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testApproval")
public class TestApprovalController {

    @Resource
    private TestApprovalService testApprovalService;

    @PostMapping("/submitApproval")
    public void submitApproval() {
        testApprovalService.submitApproval();
    }

    @PostMapping("/overrule")
    public void overrule(@RequestParam Long id, @RequestParam boolean isOverrule, @RequestParam(required = false) String reason) {
        testApprovalService.overrule(id, isOverrule, reason);
    }

    @PostMapping("/cancel")
    public void cancel(@RequestParam Long id) {
        testApprovalService.cancel(id);
    }
}
