package org.langost.scok.config.tokens;

import lombok.RequiredArgsConstructor;
import org.langost.scok.entity.RefreshToken;
import org.langost.scok.entity.User;
import org.langost.scok.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    public String createRefreshToken(User user) {
        String rawToken = UUID.randomUUID().toString() + UUID.randomUUID();
        String hash = hash(rawToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hash);
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    public User validateAndRotate(String rawToken) {
        String hash = hash(rawToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BadCredentialsException("Invalid token"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("token expired");
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return stored.getUser();
    }

    public void revokeAllForUser(User user) {
        refreshTokenRepository.findByUserAndRevokedFalse(user)
                .forEach(token->
                        {
                        token.setRevoked(true);
                        refreshTokenRepository.save(token);
                    }
                );
    }

    private String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}