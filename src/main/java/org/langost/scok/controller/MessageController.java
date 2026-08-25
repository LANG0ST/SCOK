package org.langost.scok.controller;

import lombok.RequiredArgsConstructor;
import org.langost.scok.dto.response.MessageResponse;
import org.langost.scok.entity.UserPrincipal;
import org.langost.scok.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/room/{roomId}/messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageResponse>> getRoomMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                messageService.getRoomMessages(roomId, principal.getUser().getId()));
    }
}
