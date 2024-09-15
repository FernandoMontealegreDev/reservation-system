package com.fernandomontealegre.reservationsystem.reservationsystem.repository;

// Importaciones de modelos
import com.fernandomontealegre.reservationsystem.reservationsystem.model.User;

// Importaciones de Spring Data JPA
import org.springframework.data.jpa.repository.JpaRepository;

// Importaciones de Java
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}