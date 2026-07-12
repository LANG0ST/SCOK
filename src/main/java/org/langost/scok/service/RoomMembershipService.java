package org.langost.scok.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.langost.scok.dto.response.RoomMembershipResponse;
import org.langost.scok.entity.Room;
import org.langost.scok.entity.RoomMembership;
import org.langost.scok.entity.User;
import org.langost.scok.repository.RoomMembershipRepository;
import org.langost.scok.repository.RoomRepository;
import org.langost.scok.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class RoomMembershipService {

    private final RoomMembershipRepository roomMembershipRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public RoomMembershipResponse join(Long roomId, Long userId) {

        if (roomMembershipRepository.existsByRoomIdAndUserId(roomId, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "already a member");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        RoomMembership membership = new RoomMembership();
        membership.setRoom(room);
        membership.setUser(user);
        roomMembershipRepository.save(membership);

        return toResponse(membership);
    }

    public List<RoomMembershipResponse> listMembers(Long roomId) {
        return roomMembershipRepository.findByRoomId(roomId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void quit(Long roomId, Long userId) {
        RoomMembership membership = roomMembershipRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not a member of this room"));

        roomMembershipRepository.delete(membership);
        reassignOwnerIfNeeded(roomId, userId);
    }

    public void kick(Long roomId, Long targetUserId, Long requesterId) {
        Room room = roomRepository.findByIdAndOwnerId(roomId, requesterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this room"));

        if (targetUserId.equals(requesterId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Owner cannot kick themself, use quit or delete room instead");
        }

        RoomMembership membership = roomMembershipRepository.findByRoomIdAndUserId(roomId, targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not a member of this room"));

        roomMembershipRepository.delete(membership);
    }

    private void reassignOwnerIfNeeded(Long roomId, Long departedUserId) {
        Room room = roomRepository.findById(roomId).orElseThrow();

        if (!room.getOwner().getId().equals(departedUserId)) {
            return;
        }

        List<RoomMembership> remaining = roomMembershipRepository.findByRoomIdOrderByJoinedAtAsc(roomId);

        if (remaining.isEmpty()) {
            room.setIsActive(false);
            roomRepository.save(room);
            return;
        }

        User nextOwner = remaining.getFirst().getUser();
        room.setOwner(nextOwner);
        roomRepository.save(room);
    }

    private RoomMembershipResponse toResponse(RoomMembership membership) {
        return new RoomMembershipResponse(
                membership.getUser().getId(),
                membership.getUser().getUsername(),
                membership.getJoinedAt()
        );
    }
}