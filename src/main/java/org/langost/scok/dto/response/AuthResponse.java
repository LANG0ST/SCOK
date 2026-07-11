package org.langost.scok.dto.response;

public record AuthResponse(String accessToken, String refreshToken, Long userId, String username) {}