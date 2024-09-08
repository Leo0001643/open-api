package com.platform.modules.chat.vo;
import lombok.Data;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Accessors(chain = true) // 链式调用
public class MyVo11 {
    @NotBlank(message = "地址不能为空！")
    @Pattern(regexp = "^[0-9A-Za-z\u4E00-\u9FFF]{2,60}$", message = "请填写有效地址！")
    private String address;
}
