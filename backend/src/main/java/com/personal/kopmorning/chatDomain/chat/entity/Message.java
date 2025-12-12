package com.personal.kopmorning.chatDomain.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String roomId;
    public String sender;
    public String message;
    public String sendTime;
    public ChatType chatType;

    public Message(ChatMessage chatMessage) {
        this.roomId = chatMessage.getRoomId();
        this.sender = chatMessage.getSender();
        this.message = chatMessage.getMessage();
        this.sendTime = chatMessage.getSendTime();
        this.chatType = ChatType.TALK;
    }
}
