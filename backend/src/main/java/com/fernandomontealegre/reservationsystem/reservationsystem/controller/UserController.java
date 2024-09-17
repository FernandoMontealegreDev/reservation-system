package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

import com.fernandomontealegre.reservationsystem.reservationsystem.dto.*;
import com.fernandomontealegre.reservationsystem.reservationsystem.exception.InvalidCredentialsException;
import com.fernandomontealegre.reservationsystem.reservationsystem.exception.ResourceNotFoundException;
import com.fernandomontealegre.reservationsystem.reservationsystem.model.User;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
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
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody UserProfileUpdateRequest userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Actualizar los detalles del usuario
        currentUser.setName(userDetails.getName());
        currentUser.setEmail(userDetails.getEmail());

        User updatedUser = userRepository.save(currentUser);

        // Convertir a UserResponse
        UserResponse userResponse = convertToResponse(updatedUser);

        return ResponseEntity.ok(new ApiResponse<>(true, userResponse, "Perfil actualizado exitosamente"));
    }

    // Usuario: Cambiar contraseña
    @Operation(summary = "Cambiar la contraseña del usuario actual")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest passwordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Validar la contraseña antigua
        if (!passwordEncoder.matches(passwordRequest.getOldPassword(), currentUser.getPassword())) {
            throw new InvalidCredentialsException("La contraseña antigua es incorrecta.");
        }

        // Cambiar la contraseña
        currentUser.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(currentUser);

        return ResponseEntity.ok(new ApiResponse<>(true, null, "Contraseña cambiada exitosamente"));
    }

    // Método auxiliar para convertir User a UserResponse
    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}