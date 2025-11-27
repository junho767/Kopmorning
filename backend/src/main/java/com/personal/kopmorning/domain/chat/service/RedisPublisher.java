package com.personal.kopmorning.domain.chat.service;

import com.personal.kopmorning.domain.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    // 유저가 작성한 메세지를 publish 를 통해 topic 에 전송
    public void publish(ChannelTopic channelTopic, ChatMessage chatMessage) {
        log.info("published topic : {}", channelTopic.getTopic());
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}
