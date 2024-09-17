package com.fernandomontealegre.reservationsystem.reservationsystem.repository;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Buscar reservas por ID de usuario
    List<Reservation> findByUserId(Long userId);

    // Buscar reservas por ID de servicio
    List<Reservation> findByServiceId(Long serviceId);

    // Buscar reservas por ID de usuario y estado
    List<Reservation> findByUserIdAndStatus(Long userId, String status);

    // Buscar reservas por rango de fechas
    List<Reservation> findByReservationDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Buscar reservas por estado
    List<Reservation> findByStatus(String status);
    // Método personalizado para verificar si una habitación está disponible en la fecha y hora especificadas
    @Query("SELECT CASE WHEN COUNT(r) = 0 THEN TRUE ELSE FALSE END " +
           "FROM Reservation r WHERE r.service.id = :roomId " +
           "AND r.reservationDateTime = :dateTime")
    boolean isRoomAvailable(@Param("roomId") Long roomId, 
                            @Param("dateTime") LocalDateTime dateTime);


    // Método personalizado para verificar la disponibilidad de una habitación al actualizar una reserva
    @Query("SELECT CASE WHEN COUNT(r) = 0 THEN TRUE ELSE FALSE END " +
    "FROM Reservation r WHERE r.service.id = :roomId " +
    "AND r.reservationDateTime = :dateTime " +
    "AND r.id <> :reservationId")
    boolean isRoomAvailableForUpdate(@Param("roomId") Long roomId, 
                                  @Param("dateTime") LocalDateTime dateTime, 
                                  @Param("reservationId") Long reservationId);
}