package org.langost.scok.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.langost.scok.dto.response.MessageResponse;
import org.langost.scok.entity.Message;
import org.langost.scok.entity.Room;
import org.langost.scok.repository.MessageRepository;
import org.langost.scok.repository.RoomMembershipRepository;
import org.langost.scok.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final RoomMembershipRepository roomMembershipRepository;

    public List<MessageResponse> getRoomMessages(Long roomId, Long currentUserId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        if (!room.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.GONE, "Room is no longer active");
        }

        if (!roomMembershipRepository.existsByRoomIdAndUserId(roomId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member of this room");
        }

        return messageRepository.findByRoomIdOrderBySentAtAsc(roomId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.getRoom().getId(),
                message.getSentAt()
        );
    }
}
