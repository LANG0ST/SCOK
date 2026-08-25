package org.langost.scok.dto.response;

import java.time.Instant;

public record RoomResponse(
        Long id,
        String name,
        Long ownerId,
        String ownerUsername,
        Boolean isActive,
        Instant createdAt) {
}
