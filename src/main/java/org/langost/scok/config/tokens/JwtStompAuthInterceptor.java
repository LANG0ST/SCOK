package org.langost.scok.config.tokens;


import lombok.RequiredArgsConstructor;
import org.langost.scok.config.CustomUserDetailsService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtStompAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                    message, StompHeaderAccessor.class);

            if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                String authHeader = accessor.getFirstNativeHeader("Authorization");

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    throw new BadCredentialsException("Missing access token");
                }

                String token = authHeader.substring(7);
                if (!jwtService.isTokenValid(token)) {
                    throw new BadCredentialsException("Invalid access token");
                }

                String email = jwtService.extractEmail(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                accessor.setUser(authentication);
            }

            return message;
        }
}
