package com.platform.modules.loan.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.exception.BaseException;
import com.platform.common.web.service.impl.BaseServiceImpl;
import com.platform.modules.chat.dao.ChatUserDao;
import com.platform.modules.chat.domain.ChatUser;
import com.platform.modules.chat.vo.LoanApplyVo;
import com.platform.modules.loan.dao.LoanApplyDao;
import com.platform.modules.loan.domain.ChatLoanApply;
import com.platform.modules.loan.service.LoanApplyService;
import com.platform.modules.loan.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("loanApplyService")
public class LoanApplyServiceImpl extends BaseServiceImpl<ChatLoanApply> implements LoanApplyService {

    @Resource
    private LoanApplyDao loanApplyDao;

    @Resource
    private ChatUserDao chatUserDao;

    @Override
    public void loanApply(LoanApplyVo loanApplyVo) {
        ChatLoanApply chatLoanApply = new ChatLoanApply();
        BeanUtils.copyProperties(loanApplyVo, chatLoanApply);
        chatLoanApply.setBillno(StringUtils.getBillno());
        chatLoanApply.setCreateTime(new Date());
        chatLoanApply.setUsername(loanApplyVo.getUserId());
        chatLoanApply.setUpdate_time(new Date());
        String userId = loanApplyVo.getUserId();

        QueryWrapper<ChatUser> wrapper = new QueryWrapper();
        wrapper.eq("user_id", userId);
        ChatUser chatUser = chatUserDao.selectOne(wrapper);
        Optional.ofNullable(chatUser).orElseThrow(()->new BaseException("非法申请！"));
        loanApplyDao.insert(chatLoanApply);
    }

    @Override
    public List<ChatLoanApply> list(LoanApplyVo loanApplyVo) {
        ChatLoanApply chatLoanApply = new ChatLoanApply();
        BeanUtils.copyProperties(loanApplyVo, chatLoanApply);
        return loanApplyDao.queryList(chatLoanApply);
    }

    @Override
    public ChatLoanApply getInfo(String billno) {
        ChatLoanApply chatLoanApply = new ChatLoanApply();
        chatLoanApply.setBillno(billno);
        return this.queryOne(chatLoanApply);
    }
}
