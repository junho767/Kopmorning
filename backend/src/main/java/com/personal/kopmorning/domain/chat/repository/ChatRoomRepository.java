package com.personal.kopmorning.domain.chat.repository;

import com.personal.kopmorning.domain.chat.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {
    private static final String CHAT_ROOM = "CHAT_ROOM";
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;

    public List<ChatRoom> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOM);
    }

    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOM, id);
    }

    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        opsHashChatRoom.put(CHAT_ROOM, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }
}
