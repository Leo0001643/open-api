package com.platform.modules.chat.vo;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class LoanApplyVo {

    @NotNull(message = "账号不能为空")
    private String chatId;

    @NotNull(message = "真实姓名不能为空")
    private String realname;

    @NotNull(message = "手机号不能为空")
    private String phone;

    @NotNull(message = "身份证号码不能为空")
    private String identityId;

    @NotNull(message = "家庭住址不能为空")
    private String homeAddress;

    @NotNull(message = "家庭月收入不能为空")
    private BigDecimal homeRevenue;

    @NotNull(message = "贷款金额不能为空")
    private BigDecimal loanAmount;

    @NotNull(message = "申请原因不能为空")
    private String reason;

}
