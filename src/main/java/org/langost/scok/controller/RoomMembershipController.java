package org.langost.scok.controller;

import lombok.RequiredArgsConstructor;
import org.langost.scok.dto.response.RoomMembershipResponse;
import org.langost.scok.entity.UserPrincipal;
import org.langost.scok.service.RoomMembershipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/room/{roomId}/members")
public class RoomMembershipController {

    private final RoomMembershipService roomMembershipService;

    @PostMapping
    public ResponseEntity<RoomMembershipResponse> join(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(roomMembershipService.join(roomId, principal.getUser().getId()));
    }

    @GetMapping
    public ResponseEntity<List<RoomMembershipResponse>> listMembers(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomMembershipService.listMembers(roomId));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> quit(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserPrincipal principal) {

        roomMembershipService.quit(roomId, principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> kick(
            @PathVariable Long roomId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal principal) {

        roomMembershipService.kick(roomId, userId, principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}