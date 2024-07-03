package com.platform.modules.loan.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 贷款申请表
 * @TableName loan_apply
 */
@Data
@TableName("chat_loan_apply")
@Accessors(chain = true)
public class ChatLoanApply implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账号ID
     */
    private String chatId;

    /**
     * 申请单号
     */
    private String billno;

    /**
     * 会员名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 身份证号
     */
    private String identityId;

    /**
     * 家庭住址
     */
    private String homeAddress;

    /**
     * 家庭月收入
     */
    private BigDecimal homeRevenue;

    /**
     * 贷款金额
     */
    private BigDecimal loanAmount;

    /**
     * 理由
     */
    private String reason;

    /**
     * 有效标记
     */
    private String mark;


    /**
     * 申请时间
     */
    private Date createTime;

    /**
     * '更新时间'
     */
    private Date update_time;


    /**
     * '添加人'
     */
    private Integer create_user;

    /**
     * '更新人'
     */
    private Integer update_user;


    private static final long serialVersionUID = 1L;
}