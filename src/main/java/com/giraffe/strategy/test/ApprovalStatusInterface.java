package com.giraffe.strategy.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public interface ApprovalStatusInterface {



    static ApprovalStatusInterface getNextStatusY(String strategyName, String nodeKey) {
        for (ApprovalStatusInterface status : getAllImplementations()) {
            if (Objects.equals(status.getStrategy(), strategyName) && Objects.equals(status.getNodeKey(), nodeKey)) {
                return status.getNextStatusY();
            }
        }
        return null;
    }

    static ApprovalStatusInterface getNextStatusN(String strategyName, String nodeKey) {
        for (ApprovalStatusInterface status : getAllImplementations()) {
            if (Objects.equals(status.getStrategy(), strategyName) && Objects.equals(status.getNodeKey(), nodeKey)) {
                return status.getNextStatusN();
            }
        }
        return null;
    }

    String getStrategy();
    String getNodeKey();
    ApprovalStatusInterface getNextStatusY();
    ApprovalStatusInterface getNextStatusN();

    static List<ApprovalStatusInterface> getAllImplementations() {
        List<ApprovalStatusInterface> implementations = new ArrayList<>();
        for (Class<? extends ApprovalStatusInterface> clazz : getAllImplementingClasses()) {
            if (clazz.isEnum()) {
                implementations.addAll(Arrays.asList(clazz.getEnumConstants()));
            }
        }
        return implementations;
    }

    static List<Class<? extends ApprovalStatusInterface>> getAllImplementingClasses() {
        List<Class<? extends ApprovalStatusInterface>> classes = new ArrayList<>();
        // Add all classes that implement ApprovalStatusInterface
        // This part can be done manually or by using a library like Reflections to scan the classpath
        classes.add(TestApprovalStatusEnum.class);
        return classes;
    }


}
