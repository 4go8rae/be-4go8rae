package com.team.project.controller;

import com.team.project.dto.request.ChatMessageDto;
import com.team.project.dto.request.ChatRoomDto;
import com.team.project.jwt.UserDetailsImpl;
import com.team.project.service.ChatMessageService;
import com.team.project.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat/message")
    public void chat(ChatMessageDto.Send message) {
        chatMessageService.sendMessage(message);
        messagingTemplate.convertAndSend("sub/chat/room/" + message.getRoomId(), message);
    }

    @PostMapping("/api/chat/room")
    public ResponseEntity<ChatRoomDto.Create> JoinChatRoom(@RequestBody ChatRoomDto.Request dto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(chatRoomService.joinChatRoom(dto, userDetails));
        } catch(IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/chat/room")
    public ResponseEntity<List<ChatRoomDto.Response>> getChatRoomList(@RequestParam Long memberId) {
        return ResponseEntity.ok().body(chatRoomService.getRoomList(memberId));
    }

    @GetMapping("/api/chat/room/{roomId}")
    public ResponseEntity<ChatRoomDto.Detail> getChatRoomDetail(@PathVariable Long roomId) {
        return ResponseEntity.ok().body(chatRoomService.getRoomDetail(roomId));
    }
}