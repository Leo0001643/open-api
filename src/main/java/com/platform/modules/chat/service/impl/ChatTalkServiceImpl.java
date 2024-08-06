package com.platform.modules.chat.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.enums.YesOrNoEnum;
import com.platform.common.shiro.ShiroUtils;
import com.platform.modules.chat.config.TencentConfig;
import com.platform.modules.chat.domain.ChatUser;
import com.platform.modules.chat.enums.ApplySourceEnum;
import com.platform.modules.chat.enums.FriendTypeEnum;
import com.platform.modules.chat.service.ChatTalkService;
import com.platform.modules.chat.service.ChatUserService;
import com.platform.modules.chat.service.ChatWeatherService;
import com.platform.modules.chat.utils.TencentUtils;
import com.platform.modules.chat.vo.FriendVo06;
import com.platform.modules.chat.vo.FriendVo07;
import com.platform.modules.push.vo.PushParamVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统聊天 服务层实现
 * q3z3
 * </p>
 */
@Service("chatTalkService")
public class ChatTalkServiceImpl implements ChatTalkService {

    @Autowired
    private TencentConfig tencentConfig;

    @Resource
    private ChatWeatherService weatherService;

    @Resource
    private ChatUserService chatUserService;

    /**
     * 好友列表
     */
    private  List<FriendVo06> friendList() {
       /* // 客服二
        Long weatherId = 15888888888L;
        FriendTypeEnum weatherType = FriendTypeEnum.WEATHER;
        FriendVo06 weather = new FriendVo06()
                .setUserId(weatherId)
                .setChatNo(NumberUtil.toStr(weatherId))
                .setNickName(weatherType.getInfo())
                .setPortrait("http://wd157.com/upload/onlineService/onlineService1.png")
                .setUserType(weatherType);
        // 客服二
        Long translationId = 18888888888L;
        FriendTypeEnum translationType = FriendTypeEnum.TRANSLATION;
        FriendVo06 translation = new FriendVo06()
                .setUserId(translationId)
                .setChatNo(NumberUtil.toStr(translationId))
                .setNickName(translationType.getInfo())
                .setPortrait("http://wd157.com/upload/onlineService/onlineService2.png")
                .setUserType(translationType);*/
        QueryWrapper<ChatUser> qw = new QueryWrapper<>();
        qw.in("phone", Arrays.asList("15888888888", "18888888888"));
        List<ChatUser> chatUsers = chatUserService.queryList(qw);

        return chatUsers.stream().map(item -> {
            FriendVo06 sysFriendVo = new FriendVo06()
                    .setUserId(item.getUserId())
                    .setChatNo(item.getChatNo())
                    .setNickName(item.getNickName())
                    .setPortrait(item.getPortrait())
                    .setUserType(FriendTypeEnum.SYSTEM_CUSTOMER);
            return sysFriendVo;
        }).collect(Collectors.toList());

//        return CollUtil.newArrayList(weather, translation);
    }

    @Override
    public List<FriendVo06> queryFriendList() {
        Long userId = ShiroUtils.getUserId();
        List<FriendVo06> userList = new ArrayList<>();
        if(userId != 15888888888L && userId!= 18888888888L){
            userList = friendList();
        }
        userList.add(BeanUtil.toBean(chatUserService.findById(userId), FriendVo06.class).setUserType(FriendTypeEnum.SELF));
        return userList;
    }

    @Override
    public FriendVo07 queryFriendInfo(Long userId) {
        Map<Long, FriendVo06> dataList = friendList().stream().collect(Collectors.toMap(FriendVo06::getUserId, a -> a, (k1, k2) -> k1));
        FriendVo06 friendVo = dataList.get(userId);
        if (friendVo == null) {
            return null;
        }
        return BeanUtil.toBean(friendVo, FriendVo07.class)
                .setIsFriend(YesOrNoEnum.YES)
                .setSource(ApplySourceEnum.SYS);
    }

    @Override
    public PushParamVo talk(Long userId, String content) {
        Map<Long, FriendVo06> dataList = friendList().stream().collect(Collectors.toMap(FriendVo06::getUserId, a -> a, (k1, k2) -> k1));
        FriendVo06 friendVo = dataList.get(userId);
        if (friendVo == null) {
            return null;
        }
        PushParamVo paramVo = new PushParamVo()
                .setUserId(friendVo.getUserId())
                .setPortrait(friendVo.getPortrait())
                .setNickName(friendVo.getNickName())
                .setContent(content)
                .setUserType(friendVo.getUserType());
        switch (friendVo.getUserType()) {
            case WEATHER:
                content = weather(content);
                break;
            case TRANSLATION:
                content = TencentUtils.translation(tencentConfig, content);
                break;
        }
        return paramVo.setContent(content);
    }

    /**
     * 天气预报
     */
    private String weather(String content) {
        List<JSONObject> dataList = weatherService.queryByCityName(content);
        if (CollectionUtils.isEmpty(dataList)) {
            return "暂未找到结果，格式：北京市";
        }
        StringBuilder builder = new StringBuilder();
        dataList.forEach(e -> {
            builder.append("城市：");
            builder.append(e.getStr("province"));
            builder.append(e.getStr("city"));
            builder.append("\n");
            builder.append("天气：");
            builder.append(e.getStr("weather"));
            builder.append("\n");
            builder.append("温度：");
            builder.append(e.getStr("temperature"));
            builder.append("℃");
            builder.append("\n");
            builder.append("风力：");
            builder.append(e.getStr("windpower"));
            builder.append("级");
            builder.append("\n");
            builder.append("湿度：");
            builder.append(e.getStr("temperature"));
            builder.append("RH");
            builder.append("\n");
            builder.append("\n");
        });
        return StrUtil.removeSuffix(builder.toString(), "\n\n");
    }

    public static void main(String[] args) {
        Console.log(IdUtil.simpleUUID());
    }

}
