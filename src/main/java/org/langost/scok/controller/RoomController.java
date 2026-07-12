package org.langost.scok.controller;

import lombok.RequiredArgsConstructor;
import org.langost.scok.dto.request.ChangeRoomNameRequest;
import org.langost.scok.dto.request.RoomCreationRequest;
import org.langost.scok.entity.Room;
import org.langost.scok.entity.UserPrincipal;
import org.langost.scok.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<Room> create(
            @RequestBody RoomCreationRequest request,
            @AuthenticationPrincipal UserPrincipal principal)
    {

        return ResponseEntity.ok(roomService.create(request,principal.getUser().getId()));

    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getDetails(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(roomService.getDetails(roomId, principal.getUser().getId()));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<Room>> getUserRooms(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(roomService.getUserRooms(principal.getUser().getId()));
    }

    @PatchMapping("/{roomId}")
    public ResponseEntity<Room> changeRoomName(
            @PathVariable long roomId,
            @RequestBody ChangeRoomNameRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(
                roomService.changeRoomName(roomId, request, principal.getUser().getId()));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserPrincipal principal) {

        roomService.deleteRoom(roomId,principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}

