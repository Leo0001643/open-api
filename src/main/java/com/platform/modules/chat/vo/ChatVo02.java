package com.platform.modules.chat.vo;

import com.platform.modules.push.enums.PushMsgEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ChatVo02 {

    @NotNull(message = "群组不能为空")
    private Long groupId;

    @NotNull(message = "消息类型不能为空")
    private PushMsgEnum msgType;

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 20000, message = "消息内容长度不能大于20000")
    private String content;

    //引用消息ID
    private Long refMsgId;
}
