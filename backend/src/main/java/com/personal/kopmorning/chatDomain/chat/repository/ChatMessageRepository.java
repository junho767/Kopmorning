package com.personal.kopmorning.chatDomain.chat.repository;

import com.personal.kopmorning.chatDomain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRoomIdOrderBySendTimeAsc(String roomId);
}
