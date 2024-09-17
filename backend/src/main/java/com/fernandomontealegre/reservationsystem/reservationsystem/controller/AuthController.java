package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

import com.fernandomontealegre.reservationsystem.reservationsystem.dto.*;
import com.fernandomontealegre.reservationsystem.reservationsystem.exception.InvalidCredentialsException;
import com.fernandomontealegre.reservationsystem.reservationsystem.exception.UserDisabledException;
import com.fernandomontealegre.reservationsystem.reservationsystem.model.*;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.UserRepository;
import com.fernandomontealegre.reservationsystem.reservationsystem.security.CustomUserDetailsService;
import com.fernandomontealegre.reservationsystem.reservationsystem.security.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "Endpoints para autenticación y gestión de tokens")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest userRequest) {
        logger.info("Intentando registrar un nuevo usuario: {}", userRequest.getUsername());

        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            logger.warn("El nombre de usuario ya existe: {}", userRequest.getUsername());
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "El nombre de usuario ya existe"));
        }
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            logger.warn("El correo electrónico ya está en uso: {}", userRequest.getEmail());
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "El correo electrónico ya está en uso"));
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setRole(RoleType.CLIENT);

        userRepository.save(user);
        logger.info("Usuario registrado exitosamente: {}", user.getUsername());

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseEntity.ok(new ApiResponse<>(true, userResponse, "Usuario registrado exitosamente"));
    }

    @Operation(summary = "Iniciar sesión")
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest) {
        logger.info("Intentando iniciar sesión para el usuario: {}", authenticationRequest.getUsername());

        try {
            authenticateUser(authenticationRequest.getUsername(), authenticationRequest.getPassword());
            String token = generateToken(authenticationRequest.getUsername());
            logger.info("Inicio de sesión exitoso para el usuario: {}", authenticationRequest.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(true, new JwtResponse(token), "Inicio de sesión exitoso"));
        } catch (InvalidCredentialsException | UserDisabledException e) {
            logger.error("Error durante el inicio de sesión para el usuario: {}", authenticationRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado durante el inicio de sesión para el usuario: {}", authenticationRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Error interno del servidor"));
        }
    }

    @Operation(summary = "Refrescar el token JWT")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        logger.info("Intentando refrescar el token JWT");

        String token = extractJwtFromRequest(request);
        if (token == null) {
            logger.warn("El token JWT no comienza con 'Bearer '");
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "El token JWT no comienza con 'Bearer '"));
        }

        try {
            if (jwtTokenUtil.canTokenBeRefreshed(token)) {
                String newToken = jwtTokenUtil.refreshToken(token);
                logger.info("Token JWT refrescado exitosamente");
                return ResponseEntity.ok(new ApiResponse<>(true, new JwtResponse(newToken), "Token JWT refrescado exitosamente"));
            } else {
                logger.warn("El token no puede ser refrescado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, "El token no puede ser refrescado"));
            }
        } catch (Exception e) {
            logger.error("Error al refrescar el token JWT", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, null, "Error interno del servidor"));
        }
    }

    // Métodos Auxiliares

    private void authenticateUser(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            logger.error("Usuario deshabilitado: {}", username);
            throw new UserDisabledException("Usuario deshabilitado. Contacte al administrador.");
        } catch (BadCredentialsException e) {
            logger.error("Credenciales inválidas para el usuario: {}", username);
            throw new InvalidCredentialsException("Credenciales inválidas. Verifique su nombre de usuario y contraseña.");
        }
    }

    private String generateToken(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtTokenUtil.generateToken(userDetails);
        logger.info("Token generado para el usuario: {}", username);
        return token;
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        final String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            return requestTokenHeader.substring(7);
        }
        return null;
    }
}