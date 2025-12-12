package com.personal.kopmorning.chatDomain.chat.controller;

import com.personal.kopmorning.chatDomain.chat.entity.ChatMessage;
import com.personal.kopmorning.chatDomain.chat.entity.ChatType;
import com.personal.kopmorning.chatDomain.chat.repository.ChatRoomRepository;
import com.personal.kopmorning.chatDomain.chat.service.RedisPublisher;
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
    public void receiveMessage(ChatMessage chatMessage) {
        chatRoomRepository.enterChatRoom(chatMessage.getRoomId());
        redisPublisher.publish(chatRoomRepository.getTopic(chatMessage.getRoomId()), chatMessage);
    }
}
