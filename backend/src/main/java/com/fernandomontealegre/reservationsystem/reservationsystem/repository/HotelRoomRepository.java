package com.fernandomontealegre.reservationsystem.reservationsystem.repository;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.HotelRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface HotelRoomRepository extends JpaRepository<HotelRoom, Long> {
    
    // Buscar una habitación por su número
    Optional<HotelRoom> findByRoomNumber(String roomNumber);
    
    // Buscar habitaciones por tipo
    List<HotelRoom> findByRoomType(String roomType);
}