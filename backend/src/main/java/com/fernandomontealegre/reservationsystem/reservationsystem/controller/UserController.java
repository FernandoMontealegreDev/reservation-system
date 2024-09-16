package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.User;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Endpoints para la gestión de usuarios")

public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Usuario: Actualizar su perfil
    @Operation(summary = "Actualizar el perfil del usuario actual")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody User userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Actualizar los detalles del usuario
        currentUser.setName(userDetails.getName());
        currentUser.setEmail(userDetails.getEmail());

        User updatedUser = userRepository.save(currentUser);
        return ResponseEntity.ok(updatedUser);
    }

    // Usuario: Cambiar contraseña
    @Operation(summary = "Cambiar la contraseña del usuario actual")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Validar la contraseña antigua
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            return ResponseEntity.badRequest().body("La contraseña antigua es incorrecta.");
        }

        // Cambiar la contraseña
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
        return ResponseEntity.ok("Contraseña cambiada exitosamente.");
    }
}