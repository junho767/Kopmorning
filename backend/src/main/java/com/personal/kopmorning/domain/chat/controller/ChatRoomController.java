package com.personal.kopmorning.domain.chat.controller;

import com.personal.kopmorning.domain.chat.entity.ChatRoom;
import com.personal.kopmorning.domain.chat.repository.ChatRoomRepository;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;

    @GetMapping("/rooms")
    public RsData<List<ChatRoom>> getChatRooms() {
        return new RsData<>(
            "200",
            "채팅방 목록 조회 성공",
                chatRoomRepository.findAllByCurrentMemberId()
        );
    }

    @GetMapping("/room/{roomId}")
    public RsData<ChatRoom> roomInfo(@PathVariable String roomId) {
        return new RsData<>(
                "200",
                "채팅방 접근 성공",
                chatRoomRepository.findRoomById(roomId)
        );
    }

    @PostMapping("/room/group")
    public RsData<ChatRoom> groupCreateRoom(@RequestParam String roomName) {
        return new RsData<>(
                "200",
                "채팅방 개설 성공",
                chatRoomRepository.createChatRoom(roomName)
        );
    }

    @PostMapping("/room")
    public RsData<ChatRoom> createRoom(
            @RequestParam String roomName,
            @RequestParam String sendMemberId,
            @RequestParam String receiveMemberId
    ) {
        return new RsData<>(
                "200",
                "채팅방 개설 성공",
                chatRoomRepository.create(roomName, sendMemberId, receiveMemberId)
        );
    }
}
