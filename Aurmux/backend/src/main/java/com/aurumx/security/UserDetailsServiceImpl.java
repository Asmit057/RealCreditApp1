package com.aurumx.security;

import com.aurumx.repository.CesUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final CesUserRepository cesUserRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var cesUser = cesUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(cesUser.getRole().name());
        
        return User.builder()
                .username(cesUser.getUsername())
                .password(cesUser.getPassword())
                .authorities(Collections.singletonList(authority))
                .build();
    }
}
