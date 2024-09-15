package com.fernandomontealegre.reservationsystem.reservationsystem.repository;

// Importaciones de modelos
import com.fernandomontealegre.reservationsystem.reservationsystem.model.HotelRoom;

// Importaciones de Spring Data JPA
import org.springframework.data.jpa.repository.JpaRepository;


public interface HotelRoomRepository extends JpaRepository<HotelRoom, Long> {
}