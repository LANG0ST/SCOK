package org.langost.scok.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.langost.scok.dto.response.MessageResponse;
import org.langost.scok.entity.Message;
import org.langost.scok.entity.Room;
import org.langost.scok.entity.User;
import org.langost.scok.exceptions.NotMemberException;
import org.langost.scok.exceptions.RoomInactiveException;
import org.langost.scok.exceptions.RoomNotFoundException;
import org.langost.scok.exceptions.UserNotFoundException;
import org.langost.scok.repository.MessageRepository;
import org.langost.scok.repository.RoomMembershipRepository;
import org.langost.scok.repository.RoomRepository;
import org.langost.scok.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final UserRepository userRepository;

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

    public MessageResponse sendMessage(Long roomId, String senderEmail, String content) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        if (!room.getIsActive()) {
            throw new RoomInactiveException(roomId);
        }

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new UserNotFoundException(senderEmail));

        if (!roomMembershipRepository.existsByRoomIdAndUserId(roomId, sender.getId())) {
            throw new NotMemberException(sender.getId(),roomId);
        }

        Message message = new Message();
        message.setRoom(room);
        message.setSender(sender);
        message.setContent(content);

        return toResponse(messageRepository.save(message));
    }
}
