package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.*;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.*;
import com.fernandomontealegre.reservationsystem.reservationsystem.security.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "Endpoints para autenticación y gestión de tokens")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Registrar un nuevo usuario")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("El campo email es obligatorio");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(Role.CLIENT);
        }

        userRepository.save(user);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    @Operation(summary = "Iniciar sesión")
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        try {
            authenticateUser(authenticationRequest.getUsername(), authenticationRequest.getPassword());
            String token = generateToken(authenticationRequest.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            return handleAuthException(e);
        }
    }

    @Operation(summary = "Refrescar el token JWT")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String token = extractJwtFromRequest(request);
        if (token == null) {
            return ResponseEntity.badRequest().body("El token JWT no comienza con 'Bearer '");
        }
        
        try {
            if (jwtTokenUtil.canTokenBeRefreshed(token)) {
                String newToken = jwtTokenUtil.refreshToken(token);
                return ResponseEntity.ok(new JwtResponse(newToken));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("El token no puede ser refrescado");
            }
        } catch (Exception e) {
            return handleAuthException(e);
        }
    }

    // Métodos Auxiliares

    private void authenticateUser(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USUARIO_DESHABILITADO", e);
        } catch (BadCredentialsException e) {
            throw new Exception("CREDENCIALES_INVALIDAS", e);
        }
    }

    private String generateToken(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtTokenUtil.generateToken(userDetails);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        final String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            return requestTokenHeader.substring(7);
        }
        return null;
    }

    private ResponseEntity<?> handleAuthException(Exception e) {
        if (e.getMessage().contains("USUARIO_DESHABILITADO")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario deshabilitado. Contacte al administrador.");
        } else if (e.getMessage().contains("CREDENCIALES_INVALIDAS")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas. Verifique su nombre de usuario y contraseña.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al intentar procesar su solicitud.");
        }
    }
}