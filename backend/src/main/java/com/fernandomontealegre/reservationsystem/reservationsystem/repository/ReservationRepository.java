package com.fernandomontealegre.reservationsystem.reservationsystem.repository;

// Importaciones de modelos
import com.fernandomontealegre.reservationsystem.reservationsystem.model.Reservation;

// Importaciones de Spring Framework
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Importaciones de Java
import java.util.List;


@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
}