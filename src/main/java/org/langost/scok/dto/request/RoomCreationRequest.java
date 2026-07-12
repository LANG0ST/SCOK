package org.langost.scok.dto.request;

import jakarta.validation.constraints.NotNull;

public record RoomCreationRequest(@NotNull String roomName) {
}
