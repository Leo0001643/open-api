package com.platform.modules.chat.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.platform.common.constant.AppConstants;
import com.platform.common.enums.YesOrNoEnum;
import com.platform.common.shiro.ShiroUtils;
import com.platform.common.utils.TimerUtils;
import com.platform.common.web.service.impl.BaseServiceImpl;
import com.platform.modules.chat.dao.ChatMsgDao;
import com.platform.modules.chat.dao.ChatUserDao;
import com.platform.modules.chat.domain.*;
import com.platform.modules.chat.enums.FriendTypeEnum;
import com.platform.modules.chat.enums.MsgStatusEnum;
import com.platform.modules.chat.service.*;
import com.platform.modules.chat.vo.ChatVo01;
import com.platform.modules.chat.vo.ChatVo02;
import com.platform.modules.chat.vo.ChatVo03;
import com.platform.modules.chat.vo.ChatVo04;
import com.platform.modules.push.enums.PushMsgEnum;
import com.platform.modules.push.enums.PushTalkEnum;
import com.platform.modules.push.service.ChatPushService;
import com.platform.modules.push.vo.PushParamVo;
import com.platform.modules.push.vo.RefMsgVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 聊天消息 服务层实现
 * q3z3
 * </p>
 */
@Service("chatMsgService")
public class ChatMsgServiceImpl extends BaseServiceImpl<ChatMsg> implements ChatMsgService {

    @Resource
    private ChatMsgDao chatMsgDao;
    @Resource
    private ChatUserDao chatUserDao;
    @Resource
    private ChatFriendService friendService;

    @Resource
    private ChatGroupService groupService;

    @Resource
    private ChatGroupInfoService groupInfoService;

    @Resource
    private ChatPushService chatPushService;

    @Resource
    private ChatUserService chatUserService;

    @Resource
    private ChatTalkService chatTalkService;

    @Autowired
    public void setBaseDao() {
        super.setBaseDao(chatMsgDao);
    }

    @Override
    public List<ChatMsg> queryList(ChatMsg t) {
        List<ChatMsg> dataList = chatMsgDao.queryList(t);
        return dataList;
    }

    @Transactional
    @Override
    public ChatVo03 sendFriendMsg(ChatVo01 chatVo) {
        Long userId = ShiroUtils.getUserId();
        Long friendId = chatVo.getUserId();
        // 系统好友
        if (friendId.equals(15888888888L) || friendId.equals(18888888888L)) {
            return sys(chatVo);
        }
        // 自己给自己发消息
        if (userId.equals(friendId)) {
            return self(chatVo);
        }
        // 发送给好友的消息
        return friend(chatVo);
    }

    /**
     * 保存消息
     */
    private ChatMsg saveMsg(ChatVo01 chatVo) {
        Long userId = ShiroUtils.getUserId();
        ChatMsg chatMsg = new ChatMsg()
                .setFromId(userId)
                .setToId(chatVo.getUserId())
                .setMsgType(chatVo.getMsgType())
                .setTalkType(PushTalkEnum.SINGLE)
                .setContent(chatVo.getContent())
                .setCreateTime(DateUtil.date());
        this.add(chatMsg);
        return chatMsg;
    }

    /**
     * 系统消息
     */
    private ChatVo03 sys(ChatVo01 chatVo) {
        // 保存消息
        ChatMsg chatMsg = this.saveMsg(chatVo);
        Long userId = ShiroUtils.getUserId();
        Long friendId = chatVo.getUserId();
        String content = chatVo.getContent();
        PushMsgEnum msgType = chatVo.getMsgType();
        Long refMsgId = chatVo.getRefMsgId();

/*        // 异步执行
        TimerUtils.instance().addTask((timeout) -> {
            // 发送聊天
            PushParamVo paramVo = chatTalkService.talk(friendId, content);
            if (paramVo == null) {
                return;
            }
            // 推送
            chatPushService.pushMsg(paramVo.setToId(userId).setMsgId(IdWorker.getId()), PushMsgEnum.TEXT);
        }, 2, TimeUnit.SECONDS);*/

        RefMsgVo refMsgVo = new RefMsgVo();
        if(refMsgId != null){
            ChatMsg refChatMsg = chatMsgDao.selectById(refMsgId);
            ChatUser chatUser = chatUserDao.selectById(refChatMsg.getFromId());
            refMsgVo.setMsgType(refChatMsg.getMsgType());
            refMsgVo.setMsgId(refChatMsg.getId());
            refMsgVo.setContent(refChatMsg.getContent());
            refMsgVo.setNickName(chatUser.getNickName());
        }

        ChatUser systemUser = chatUserService.getById(friendId);
        PushParamVo paramVo = ChatUser.initParam(chatUserService.getById(userId))
                .setNickName(systemUser.getNickName())
                .setTop(YesOrNoEnum.YES)
                .setContent(content)
                .setToId(friendId)
                .setMsgId(chatMsg.getId())
                .setRefMsg(refMsgVo);
        chatPushService.pushMsg(paramVo.setToId(userId).setMsgId(IdWorker.getId()), msgType);
        // 返回结果
        return doResult(MsgStatusEnum.NORMAL).setMsgId(chatMsg.getId());
    }

    /**
     * 自己消息
     */
    private ChatVo03 self(ChatVo01 chatVo) {
        // 保存消息
        ChatMsg chatMsg = this.saveMsg(chatVo);
        Long userId = ShiroUtils.getUserId();
        Long friendId = chatVo.getUserId();
        String content = chatVo.getContent();
        PushMsgEnum msgType = chatVo.getMsgType();
        // 组装推送
        PushParamVo paramVo = ChatUser.initParam(chatUserService.getById(userId))
                .setUserType(FriendTypeEnum.SELF)
                .setContent(content)
                .setToId(friendId)
                .setMsgId(IdWorker.getId());
        // 推送
        // 异步执行
        TimerUtils.instance().addTask((timeout) -> {
            // 推送
            chatPushService.pushMsg(paramVo, msgType);
        }, 2, TimeUnit.SECONDS);
        // 返回结果
        return doResult(MsgStatusEnum.NORMAL)
                .setMsgId(chatMsg.getId());
    }

    /**
     * 好友消息
     */
    private ChatVo03 friend(ChatVo01 chatVo) {
        Long userId = ShiroUtils.getUserId();
        Long friendId = chatVo.getUserId();
        String content = chatVo.getContent();
        PushMsgEnum msgType = chatVo.getMsgType();
        Long refmsgId = chatVo.getRefMsgId();

        // 校验好友
        ChatFriend friend1 = friendService.getFriend(userId, friendId);
        if (friend1 == null) {
            return doResult(MsgStatusEnum.FRIEND_TO);
        }
        // 校验好友
        ChatFriend friend2 = friendService.getFriend(friendId, userId);
        if (friend2 == null) {
            return doResult(MsgStatusEnum.FRIEND_FROM);
        }
        // 校验黑名单
        if (YesOrNoEnum.YES.equals(friend2.getBlack())) {
            return doResult(MsgStatusEnum.FRIEND_BLACK);
        }
        // 校验好友
        ChatUser toUser = chatUserService.getById(friendId);
        if (toUser == null) {
            return doResult(MsgStatusEnum.FRIEND_DELETED);
        }

        RefMsgVo refMsgVo = new RefMsgVo();
        if(refmsgId != null){
            ChatMsg refChatMsg = chatMsgDao.selectById(refmsgId);
            ChatUser chatUser = chatUserDao.selectById(refChatMsg.getFromId());
            refMsgVo.setMsgType(refChatMsg.getMsgType());
            refMsgVo.setMsgId(refChatMsg.getId());
            refMsgVo.setContent(refChatMsg.getContent());
            refMsgVo.setNickName(chatUser.getNickName());
        }
        // 保存消息
        ChatMsg chatMsg = this.saveMsg(chatVo);
        // 组装推送
        PushParamVo paramVo = ChatUser.initParam(chatUserService.getById(userId))
                .setNickName(friend2.getRemark())
                .setTop(friend2.getTop())
                .setContent(content)
                .setToId(friendId)
                .setMsgId(chatMsg.getId())
                .setRefMsg(refMsgVo);

        ChatVo04 chatVo04 = null;
        if (PushMsgEnum.TRTC_VOICE_START.equals(msgType) || PushMsgEnum.TRTC_VIDEO_START.equals(msgType)) {
            chatVo04 = new ChatVo04()
                    .setUserId(friendId)
                    .setTrtcId(AppConstants.REDIS_TRTC_USER + friendId)
                    .setPortrait(toUser.getPortrait())
                    .setNickName(friend1.getRemark());
        }
        // 推送
        chatPushService.pushMsg(paramVo, msgType);
        return doResult(MsgStatusEnum.NORMAL)
                .setMsgId(chatMsg.getId())
                .setUserInfo(chatVo04);
    }


    /**
     * 返回发送结果
     */
    private ChatVo03 doResult(MsgStatusEnum status) {
        return new ChatVo03().setStatus(status);
    }

    @Override
    public ChatVo03 sendGroupMsg(ChatVo02 chatVo) {
        String content = chatVo.getContent();
        Long fromId = ShiroUtils.getUserId();
        Long groupId = chatVo.getGroupId();
        Long refMsgId = chatVo.getRefMsgId();

        // 查询群组
        ChatGroup group = groupService.getById(groupId);
        if (group == null) {
            return doResult(MsgStatusEnum.GROUP_NOT_EXIST);
        }
        // 查询群明细
        ChatGroupInfo groupInfo = groupInfoService.getGroupInfo(groupId, fromId, YesOrNoEnum.NO);
        if (groupInfo == null) {
            return doResult(MsgStatusEnum.GROUP_INFO_NOT_EXIST);
        }
        //查询引用消息
        RefMsgVo refMsgVo = new RefMsgVo();
        if(refMsgId != null){
            ChatMsg refChatMsg = chatMsgDao.selectById(refMsgId);
            ChatUser chatUser = chatUserDao.selectById(refChatMsg.getFromId());
            refMsgVo.setMsgType(refChatMsg.getMsgType());
            refMsgVo.setMsgId(refChatMsg.getId());
            refMsgVo.setContent(refChatMsg.getContent());
            refMsgVo.setNickName(chatUser.getNickName());
        }

        // 保存数据
        ChatMsg chatMsg = new ChatMsg()
                .setFromId(fromId)
                .setToId(groupId)
                .setMsgType(chatVo.getMsgType())
                .setTalkType(PushTalkEnum.GROUP)
                .setContent(content)
                .setCreateTime(DateUtil.date());
        this.add(chatMsg);
        // 查询群列表
        List<PushParamVo> userList = groupService.queryFriendPushFrom(chatMsg);
        // 群信息
        PushParamVo groupUser = new PushParamVo()
                .setUserId(group.getId())
                .setNickName(group.getName())
                .setRefMsg(refMsgVo)
                .setPortrait(group.getPortrait());
        // 推送
        chatPushService.pushGroupMsg(userList, groupUser, chatVo.getMsgType());
        return doResult(MsgStatusEnum.NORMAL)
                .setMsgId(chatMsg.getId());
    }

}
