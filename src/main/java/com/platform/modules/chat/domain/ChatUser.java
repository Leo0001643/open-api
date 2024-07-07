package com.platform.modules.chat.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.constant.AppConstants;
import com.platform.common.enums.GenderEnum;
import com.platform.common.web.domain.BaseEntity;
import com.platform.modules.push.vo.PushParamVo;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 用户表实体类
 * q3z3
 * </p>
 */
@Data
@TableName("chat_user")
@Accessors(chain = true)
public class ChatUser extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 会员ID
     */
    private Integer id;

    /**
     * 主键
     */
    @TableId
    private Long userId;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 用户名
     */
    private String username;
    /**
     * 邮箱
     */
    private String email;

    /**
     * 微聊号码
     */
    private String chatNo;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户token
     */
    private String token;

    /**
     * 性别：1男 2女 3未知
     */
    private String gender;

    /**
     * 头像
     */
    private String portrait;

    /**
     * 介绍
     */
    private String intro;

    /**
     * 封面
     */
    private String cover;

    /**
     * 省份
     */
    private String provinces;

    /**
     * 城市
     */
    private String city;

    /**
     * 盐
     */
    private String salt;

    /**
     * 用户来源：1注册会员 2马甲会员
     */
    private Integer source;

    /**
     * 设备类型：1苹果 2安卓 3WAP站 4PC站 5微信小程序 6后台添加
     */
    private Integer device;

    /**
     * 用户状态：1登录 2登出
     */
    private Integer loginStatus;

    /**
     * 是否启用：1启用  2停用
     */
    private Integer status;

    /**
     * 我的推广码
     */
    private String code;

    /**
     * 推广二维码路径
     */
    private String qrcode;

    /**
     * 最近登录IP
     */
    private String loginIp;

    /**
     * 上次登录地点
     */
    private String loginRegion;

    /**
     * 最近登录时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date loginTime;

    /**
     * 登录总次数
     */
    private Integer loginCount;
    /**
     * 版本信息
     */
    private String version;
    /**
     * 注册时间
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Date createTime;
    /**
     * 注销0正常null注销
     */
    @TableLogic
    private Integer deleted;
    /**
     * 注销时间
     */
    private Date deletedTime;

    /**
     * 格式化，防止出错
     */
    public static ChatUser initUser(ChatUser user) {
        if (user != null) {
            return user;
        }
        return new ChatUser()
                .setGender(GenderEnum.MALE.getCode())
                .setLoginCount(1)
                .setLoginTime(new Date())
                .setStatus(1)
                .setDeleted(0)
                .setPortrait(AppConstants.DEFAULT_PORTRAIT)
                .setNickName(AppConstants.DELETED_NICK_NAME);
    }

    /**
     * 格式化，防止出错
     */
    public static PushParamVo initParam(ChatUser user) {
        user = initUser(user);
        return new PushParamVo()
                .setUserId(user.getUserId())
                .setPortrait(user.getPortrait())
                .setNickName(user.getNickName());
    }

}
