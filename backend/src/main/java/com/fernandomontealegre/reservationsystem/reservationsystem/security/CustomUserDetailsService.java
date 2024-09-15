package com.fernandomontealegre.reservationsystem.reservationsystem.security;

// Importaciones de repositorios
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.UserRepository;

// Importaciones de Spring Security
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Usuario no encontrado: " + username));
    }
}