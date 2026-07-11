package org.langost.scok.config;

import lombok.RequiredArgsConstructor;
import org.langost.scok.entity.User;
import org.langost.scok.entity.UserPrincipal;
import org.langost.scok.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("no users with this email"));

        return new UserPrincipal(user);

    }
}





