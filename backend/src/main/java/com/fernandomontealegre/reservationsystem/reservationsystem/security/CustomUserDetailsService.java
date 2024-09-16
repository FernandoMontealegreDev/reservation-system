package com.fernandomontealegre.reservationsystem.reservationsystem.security;

import com.fernandomontealegre.reservationsystem.reservationsystem.repository.UserRepository;
import com.fernandomontealegre.reservationsystem.reservationsystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUserByUsername(username);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> {
                logger.warn("Usuario no encontrado: " + username);
                return new UsernameNotFoundException("Usuario no encontrado: " + username);
            });
    }
}