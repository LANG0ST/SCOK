package org.langost.scok.dto.response;

import java.time.Instant;

public record RoomMembershipResponse(Long userId, String username, Instant joinedAt) {}