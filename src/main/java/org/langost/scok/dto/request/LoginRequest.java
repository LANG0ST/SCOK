package org.langost.scok.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(@Email @NotBlank String email,
                           @NotBlank @Size(min = 8) String password) {}
