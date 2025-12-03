package com.personal.kopmorning.domain.chat.repository;

import com.personal.kopmorning.domain.chat.entity.ChatRoom;
import com.personal.kopmorning.domain.chat.service.RedisSubscriber;
import com.personal.kopmorning.global.utils.SecurityUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {
    // 채팅방에 발행되는 메세지 처리할 Listener
    private final RedisMessageListenerContainer redisMessageListener;
    private final RedisSubscriber subscriber;

    private static final String CHAT_ROOM = "CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;

    // 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic 정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
    private Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    public List<ChatRoom> findAllByCurrentMemberId() {
        Long currentMemberId = SecurityUtil.getCurrentMember().getId();
        return opsHashChatRoom.values(CHAT_ROOM).stream()
                .filter(chatRoom -> chatRoom.getSendMemberId().equals(currentMemberId)
                        || chatRoom.getReceiveMemberId().equals(currentMemberId))
                .toList();
    }

    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOM, id);
    }

    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        opsHashChatRoom.put(CHAT_ROOM, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    public ChatRoom create(String name, String sendMemberId, String receiveMemberId) {
        ChatRoom chatRoom = ChatRoom.create(name, sendMemberId, receiveMemberId);
        opsHashChatRoom.put(CHAT_ROOM, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    // 중복 sub 방지
    public boolean enterChatRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);

        if (topic == null) {
            topic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(subscriber, topic);
            topics.put(roomId, topic);
            return true;
        } else {
            return false;
        }
    }

    public ChannelTopic getTopic(String roomId) {
        ChannelTopic topic = topics.get(roomId);
        if (topic == null) {
            log.warn("채팅방을 찾을 수 없습니다.");
        }
        return topic;
    }
}
