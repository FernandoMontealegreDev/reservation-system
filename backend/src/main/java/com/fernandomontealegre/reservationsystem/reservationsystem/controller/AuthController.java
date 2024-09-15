package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

// Importaciones de modelos, repositorios y seguridad
import com.fernandomontealegre.reservationsystem.reservationsystem.model.*;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.*;
import com.fernandomontealegre.reservationsystem.reservationsystem.security.*;

// Importaciones de Spring Framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

// Importaciones de Jakarta Validation
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
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

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) throws Exception {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }
    
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("El campo email es obligatorio");
        }
    
        // Cifrar la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    
        // Asignar rol CLIENT por defecto si no se proporciona uno
        if (user.getRole() == null) {
            user.setRole(Role.CLIENT);
        }
    
        userRepository.save(user);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }
    

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USUARIO_DESHABILITADO", e);
        } catch (BadCredentialsException e) {
            throw new Exception("CREDENCIALES_INVALIDAS", e);
        }
    }
}