package com.personal.kopmorning.domain.chat.controller;

import com.personal.kopmorning.domain.chat.entity.ChatMessage;
import com.personal.kopmorning.domain.chat.entity.ChatType;
import com.personal.kopmorning.domain.chat.repository.ChatRoomRepository;
import com.personal.kopmorning.domain.chat.service.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;

    // /pub/chat/message 로 들어오는 메세지 처리
    @MessageMapping("/chat/message")
    @SendTo("/sub/chat/{roomId}")
    public void receiveMessage(ChatMessage message) {
        if (ChatType.ENTER.equals(message.getChatType())) {
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + " 님이 입장하셨습니다.");
        }
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }
}
