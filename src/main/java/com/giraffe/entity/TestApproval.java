package com.giraffe.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestApproval {

    @TableId(type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;


    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;


    private Integer approvalStatus;

    public TestApproval() {}
}
