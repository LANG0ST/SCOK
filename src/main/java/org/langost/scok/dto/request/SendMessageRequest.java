package org.langost.scok.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(@NotBlank String content) {
}
