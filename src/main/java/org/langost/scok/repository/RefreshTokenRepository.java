package org.langost.scok.repository;

import org.langost.scok.entity.RefreshToken;
import org.langost.scok.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {


    Optional<RefreshToken> findByTokenHash(String hash);

    List<RefreshToken> findByUserAndRevokedFalse(User user);
}
