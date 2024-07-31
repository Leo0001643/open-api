package com.platform.modules.push.vo;

import com.platform.modules.push.enums.PushMsgEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true) // 链式调用
@AllArgsConstructor
@NoArgsConstructor
public class RefMsgVo {
    private Long msgId;
    private String content;
    private PushMsgEnum msgType;
}
