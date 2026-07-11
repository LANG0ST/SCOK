package org.langost.scok.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.langost.scok.config.tokens.JwtService;
import org.langost.scok.config.tokens.RefreshTokenService;
import org.langost.scok.dto.request.LoginRequest;
import org.langost.scok.dto.request.RefreshRequest;
import org.langost.scok.dto.request.SignUpRequest;
import org.langost.scok.dto.response.AuthResponse;
import org.langost.scok.dto.response.SignUpResponse;
import org.langost.scok.entity.User;
import org.langost.scok.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest loginRequest){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(),loginRequest.password())
        );

        User user = userRepository.findByEmail(loginRequest.email())
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return issueTokens(user);
    }


    @Transactional
    public SignUpResponse signup(SignUpRequest signUpRequest){

        if(userRepository.existsByEmail(signUpRequest.email())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"user deja kayn");
        }

        User user = new User();
        user.setEmail(signUpRequest.email());
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.password()));
        user.setUsername(signUpRequest.username());
        userRepository.save(user);

        return new SignUpResponse(user.getId(),user.getUsername());
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest refreshRequest){
        User user = refreshTokenService.validateAndRotate(refreshRequest.refreshToken());
        return issueTokens(user);
    }

    @Transactional
    public void logout(User user){
        refreshTokenService.revokeAllForUser(user);
    }

    private AuthResponse issueTokens(User user){
        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);
        return new AuthResponse(accessToken,refreshToken,user.getId(), user.getUsername());
    }
}

