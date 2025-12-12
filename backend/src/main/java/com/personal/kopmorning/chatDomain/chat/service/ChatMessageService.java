package com.personal.kopmorning.chatDomain.chat.service;

import com.personal.kopmorning.chatDomain.chat.entity.ChatMessage;
import com.personal.kopmorning.chatDomain.chat.entity.Message;
import com.personal.kopmorning.chatDomain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    public List<Message> getMessagesByRoom(String roomId) {
        return chatMessageRepository.findByRoomIdOrderBySendTimeAsc(roomId);
    }

    public void saveMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(new Message(chatMessage));
    }
}
