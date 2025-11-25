package com.personal.kopmorning.domain.chat.controller;

import com.personal.kopmorning.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/send") // 클라이언트가 특정 엔드포인트로 전송한 메시지를 서버의 해당 메서드가 처리하도록 매핑
    @SendTo("/sub/message") // 반환값을 특정 목적지로 전달, '/sub/messages' 를 구독한 모든 클라이언트에게 해당 메시지를 전달하게 됩니다.
    public String sendMessage(String input) {
        log.info("[info] 메세지 들어옴 내용 : {}", input);

        return input;
    }
}
