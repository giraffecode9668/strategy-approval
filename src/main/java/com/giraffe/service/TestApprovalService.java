package com.giraffe.service;

import com.giraffe.dao.TestApprovalRepository;
import com.giraffe.entity.TestApproval;
import com.giraffe.strategy.frame.CustomApprovalDrawBO;
import com.giraffe.strategy.test.AbstractTestApprovalStrategy;
import com.giraffe.strategy.test.TestApprovalCustomApprovalBO;
import com.giraffe.strategy.test.TestApprovalStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class TestApprovalService {

    @Resource
    private TestApprovalRepository testApprovalRepository;
    @Resource
    private CustomApproveService customApproveService;


    public void submitApproval() {

        TestApproval testApproval = new TestApproval();
        testApproval.setApprovalStatus(TestApprovalStatusEnum.INIT_SUBMIT.getCode());
        testApprovalRepository.save(testApproval);

        // 提交自定义节点流程
        TestApprovalCustomApprovalBO approvalBO = new TestApprovalCustomApprovalBO(
                TestApprovalStatusEnum.INIT_SUBMIT.getStrategy(),
                AbstractTestApprovalStrategy.TEST_APPROVAL_KEY,
                testApproval.getId(),
                TestApprovalStatusEnum.INIT_SUBMIT.getCode().toString(),
                testApproval
        );
        customApproveService.doCreate(approvalBO);


    }

    public void overrule(Long id, boolean isOverrule, String reason) {
        TestApproval testApproval = testApprovalRepository.getById(id);

        Integer approvalStatus = testApproval.getApprovalStatus();
        TestApprovalStatusEnum statusEnum = TestApprovalStatusEnum.getByCode(approvalStatus);

        // 提交自定义节点流程
        TestApprovalCustomApprovalBO approvalBO = new TestApprovalCustomApprovalBO(
                statusEnum.getStrategy(),
                AbstractTestApprovalStrategy.TEST_APPROVAL_KEY,
                testApproval.getId(),
                statusEnum.getCode().toString(),
                testApproval
        );
        customApproveService.doApprove(approvalBO, isOverrule, reason, null);

    }


    public void cancel(Long id) {

        TestApproval testApproval = testApprovalRepository.getById(id);

        // 提交自定义节点流程
        TestApprovalCustomApprovalBO approvalBO = new TestApprovalCustomApprovalBO(
                TestApprovalStatusEnum.CANCEL.getStrategy(),
                AbstractTestApprovalStrategy.TEST_APPROVAL_KEY,
                testApproval.getId(),
                TestApprovalStatusEnum.CANCEL.getCode().toString(),
                testApproval
        );
        customApproveService.doApprove(approvalBO, true, "", null);

    }

    public String drawSequence(Long id) {
        TestApproval testApproval = testApprovalRepository.getById(id);

        // 提交自定义节点流程
        TestApprovalStatusEnum statusEnum = TestApprovalStatusEnum.getByCode(testApproval.getApprovalStatus());

        CustomApprovalDrawBO drawBO = new CustomApprovalDrawBO(statusEnum.getStrategy(), statusEnum.getDefinitionKey(), testApproval.getId(), statusEnum.getNodeKey());
        return customApproveService.doGetDrawSequenceMermaidGrammar(drawBO);
    }
}
