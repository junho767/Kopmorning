package com.personal.kopmorning.domain.chat.entity;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
public class ChatRoom implements Serializable { //  Serializable은 객체를 바이트 스트림으로 직렬화 가능
    // 직렬화된 객체의 버전 관리를 위한 식별자
    @Serial
    private static final long serialVersionUID = 4564516516541652156L;

    private String roomId;
    private String name;

    public static ChatRoom create(String name) {
        ChatRoom chatRoom = new ChatRoom();

        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.name = name;

        return chatRoom;
    }
}
