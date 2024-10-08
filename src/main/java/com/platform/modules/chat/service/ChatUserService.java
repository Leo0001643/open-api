package com.platform.modules.chat.service;
import com.platform.common.web.service.BaseService;
import com.platform.modules.auth.vo.AuthVo01;
import com.platform.modules.chat.domain.ChatUser;
import com.platform.modules.chat.vo.MyVo09;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * <p>
 * 用户表 服务层
 * q3z3
 * </p>
 */
public interface ChatUserService extends BaseService<ChatUser> {

    /**
     * 通过手机+密码注册
     */
    void register(AuthVo01 authVo);

    /**
     * 通过手机号查询
     */
    ChatUser queryByPhone(String phone);

    /**
     * 重置密码
     */
    void resetPass(Long userId, String password);

    /**
     * 修改密码
     */
    void editPass(String password, String pwd);

    /**
     * 修改微聊号
     */
    void editChatNo(String chatNo);

    /**
     * 修改手机号
     */
    void editPhone(String phone);

    /**
     * 获取基本信息
     */
    MyVo09 getInfo();

    /**
     * 获取二维码
     */
    String getQrCode();

    /**
     * 用户注销
     */
    void deleted();

    /**
     * 执行登录/返回token
     */
    MyVo09 doLogin(AuthenticationToken authenticationToken);

    /**
     * 退出登录
     */
    void logout();

    /**
     * 刷新
     */
    void refresh();

}
