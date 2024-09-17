package com.fernandomontealegre.reservationsystem.reservationsystem.repository;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar un usuario por su nombre de usuario
    Optional<User> findByUsername(String username);

    // Buscar un usuario por su correo electrónico
    Optional<User> findByEmail(String email);

    // Verificar si un nombre de usuario ya existe
    boolean existsByUsername(String username);

    // Verificar si un correo electrónico ya existe
    boolean existsByEmail(String email);
}