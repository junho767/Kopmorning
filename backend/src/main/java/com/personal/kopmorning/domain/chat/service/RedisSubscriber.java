package com.personal.kopmorning.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.kopmorning.domain.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messageSendingOperations;

    /*
     리스너에서 감지하여 받아온 메세지를 역직렬화 후 메세지를 채널 구독하는 모든 유저에게 전달하게 됨
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis 에서 받은 바이트 배열 형태의 메세지를 String 으로 변환
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

            // objectMapper 를 통해 JSON
            ChatMessage roomMessage = objectMapper.readValue(publishMessage, ChatMessage.class);

            messageSendingOperations.convertAndSend("/sub/chat/" + roomMessage.getRoomId(), roomMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
