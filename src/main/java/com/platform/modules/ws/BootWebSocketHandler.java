package com.platform.modules.ws;
import com.platform.common.constant.AppConstants;
import com.platform.modules.push.service.ChatPushService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class BootWebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<Long, WebSocketSession> POOL_SESSION = new ConcurrentHashMap<>();

    @Resource
    private ChatPushService chatPushService;

    /**
     * socket 建立成功事件
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 用户id
        Long userId = (Long) session.getAttributes().get(AppConstants.USER_ID);
        // 存储
        POOL_SESSION.put(userId, session);
        log.info("会话池添加会话userID:{}", userId);
        // 离线消息
        chatPushService.pullMsg(userId);
    }

    /**
     * 接收消息事件
     */
    @SneakyThrows
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 获得客户端传来的消息
        String payload = message.getPayload();
        log.info("server 接收到消息 {}", payload);

        Long userId = (Long) session.getAttributes().get(AppConstants.USER_ID);
        POOL_SESSION.put(userId, session);

        session.sendMessage(new TextMessage("ok"));
    }

    /**
     * socket 断开连接时
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("连接超时断开了>>>>ID={},code={}",session.getId(), status.getCode());
        this.closeSession(session);
    }

    /**
     * socket 异常连接时
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.info("连接异常断开了>>>>ID={},code={}",session.getId(), exception.getLocalizedMessage());
        this.closeSession(session);
    }

    /**
     * 关闭session
     */
    private void closeSession(WebSocketSession session) {
        // 用户id
        Long userId = (Long) session.getAttributes().get(AppConstants.USER_ID);
        if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
            }
        }
        // 移除
        POOL_SESSION.remove(userId);
        log.info("会话池移除会话userID:{}，当前会话池存在的ID:{}", userId, POOL_SESSION);
    }

    /**
     * 给某个用户发送消息
     */
    public void sendMsg(Long userId, String content) {
        log.info("正在推送消息：用户ID:{},消息内容:{}",userId, content);
        WebSocketSession session = POOL_SESSION.get(userId);
        log.info("当前ID:{},会话池:{}", userId, POOL_SESSION);
        if (session == null) {
            return;
        }
        if (!session.isOpen()) {
            this.closeSession(session);
            return;
        }
        try {
            session.sendMessage(new TextMessage(content));
            log.info("推送消息成功：用户ID:{},消息内容:{}", content);
        } catch (IOException e) {
            this.closeSession(session);
        }
    }

}