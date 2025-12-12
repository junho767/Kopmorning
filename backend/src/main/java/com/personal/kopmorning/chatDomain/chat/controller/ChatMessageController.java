package com.personal.kopmorning.chatDomain.chat.controller;

import com.personal.kopmorning.chatDomain.chat.entity.Message;
import com.personal.kopmorning.chatDomain.chat.service.ChatMessageService;
import com.personal.kopmorning.global.entity.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @GetMapping("/room")
    public RsData<List<Message>> getMessagesByRoom(
            @RequestParam String roomId
    ) {
        return new RsData<>(
                "200",
                "메세지 목록 불러오기 성공",
                chatMessageService.getMessagesByRoom(roomId)
        );
    }
}
