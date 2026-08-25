package org.langost.scok.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChangeRoomNameRequest(@NotNull long roomId,
                                    @NotNull String newName) {
}
