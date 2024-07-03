package com.platform.modules.loan.service;
import com.platform.common.web.service.BaseService;
import com.platform.modules.chat.vo.LoanApplyVo;
import com.platform.modules.loan.domain.ChatLoanApply;

import java.util.List;

public interface LoanApplyService extends BaseService<ChatLoanApply> {
    
    void loanApply(LoanApplyVo loanApplyVo);

    List<ChatLoanApply> list(LoanApplyVo loanApplyVo);

    ChatLoanApply getInfo(String billno);
}
