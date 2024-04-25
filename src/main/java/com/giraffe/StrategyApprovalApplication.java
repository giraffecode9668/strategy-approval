package com.giraffe;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = {"com.giraffe.**.mapper"})
@SpringBootApplication
public class StrategyApprovalApplication {

    public static void main(String[] args) {
        SpringApplication.run(StrategyApprovalApplication.class, args);
    }

}
