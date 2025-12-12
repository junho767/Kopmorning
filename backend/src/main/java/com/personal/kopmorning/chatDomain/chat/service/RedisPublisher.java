package com.personal.kopmorning.chatDomain.chat.service;

import com.personal.kopmorning.chatDomain.chat.entity.ChatMessage;
import com.personal.kopmorning.chatDomain.chat.entity.ChatType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPublisher {
    private final ChatMessageService chatMessageService;
    private final RedisTemplate<String, Object> redisTemplate;

    // 유저가 작성한 메세지를 publish 를 통해 topic 에 전송
    public void publish(ChannelTopic channelTopic, ChatMessage chatMessage) {
        chatMessage.setSendTime(String.valueOf(LocalDateTime.now()));
        if (!ChatType.ENTER.equals(chatMessage.getChatType())) {
            chatMessageService.saveMessage(chatMessage);
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}
