package com.platform.modules.ws;

import cn.hutool.extra.spring.SpringUtil;
import com.platform.common.constant.AppConstants;
import com.platform.common.constant.HeadConstant;
import com.platform.common.shiro.LoginUser;
import com.platform.modules.auth.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
@Slf4j
@Component
public class BootWebSocketInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        // 接受前端传来的参数
        String token = ((ServletServerHttpRequest) request).getServletRequest().getParameter(HeadConstant.TOKEN_HEADER_ADMIN);

        log.info("我来了>>>>token={}", token);
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        TokenService tokenService = SpringUtil.getBean("tokenService");
        LoginUser loginUser = tokenService.queryByToken(token);
        if (loginUser == null) {
            return false;
        }
        //将参数放到attributes
        attributes.put(AppConstants.USER_ID, loginUser.getUserId());
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

}
