package com.linkbharat.linkbharatbackend.security;

import com.linkbharat.linkbharatbackend.domain.entity.AuthUser;
import com.linkbharat.linkbharatbackend.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthUserRepository authUserRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        AuthUser authUser = authUserRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username/email: " + identifier));

        return buildUserDetails(authUser);
    }

    public UserDetails loadUserByUsernameOrEmail(String identifier) {
        AuthUser authUser = authUserRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username/email: " + identifier));

        return buildUserDetails(authUser);
    }

    public UserDetails loadUserById(Long id) {
        AuthUser authUser = authUserRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        return buildUserDetails(authUser);
    }

    private UserDetails buildUserDetails(AuthUser authUser) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + authUser.getRole().name())
        );

        return new User(
                authUser.getUsername(),
                authUser.getPassword(),
                authUser.isEnabled(),
                true,
                true,
                true,
                authorities
        );
    }
}