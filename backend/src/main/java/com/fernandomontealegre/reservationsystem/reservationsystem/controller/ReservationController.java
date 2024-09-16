package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

import com.fernandomontealegre.reservationsystem.reservationsystem.exception.ResourceNotFoundException;
import com.fernandomontealegre.reservationsystem.reservationsystem.model.*;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation Controller", description = "Endpoints para la gestión de reservas de habitaciones")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    // Cliente: Crear una reserva
    @Operation(summary = "Crear una nueva reserva")
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody Reservation reservation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        reservation.setUser(user);

        // Validar que la habitación existe
        HotelRoom room = hotelRoomRepository.findById(reservation.getService().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada"));

        // Validar que la fecha de la reserva no sea en el pasado
        if (reservation.getReservationDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("La fecha de la reserva no puede ser en el pasado.");
        }

        // Validar si la habitación está disponible para la fecha solicitada
        List<Reservation> existingReservations = reservationRepository.findByServiceId(room.getId());
        for (Reservation existingReservation : existingReservations) {
            if (existingReservation.getReservationDateTime().equals(reservation.getReservationDateTime())) {
                return ResponseEntity.badRequest().body("La habitación ya está reservada para la fecha y hora seleccionada.");
            }
        }

        reservation.setService(room);
        reservation.setStatus("PENDING");

        Reservation savedReservation = reservationRepository.save(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    // Cliente: Ver sus propias reservas
    @Operation(summary = "Ver las reservas del usuario actual")
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/my")
    public ResponseEntity<?> getMyReservations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<Reservation> reservations = reservationRepository.findByUserId(user.getId());
        return ResponseEntity.ok(reservations);
    }

    // Cliente: Modificar su reserva
    @Operation(summary = "Actualizar la reserva del usuario actual")
    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMyReservation(@PathVariable Long id, @Valid @RequestBody Reservation reservationDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (!reservation.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tiene permiso para modificar esta reserva.");
        }

        // Validar que la nueva fecha de la reserva no sea en el pasado
        if (reservationDetails.getReservationDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("La nueva fecha de la reserva no puede ser en el pasado.");
        }

        // Validar si la habitación está disponible para la nueva fecha de la reserva
        List<Reservation> existingReservations = reservationRepository.findByServiceId(reservation.getService().getId());
        for (Reservation existingReservation : existingReservations) {
            if (!existingReservation.getId().equals(reservation.getId()) &&
                existingReservation.getReservationDateTime().equals(reservationDetails.getReservationDateTime())) {
                return ResponseEntity.badRequest().body("La habitación ya está reservada para la nueva fecha y hora seleccionada.");
            }
        }

        // Actualizar los detalles de la reserva
        reservation.setReservationDateTime(reservationDetails.getReservationDateTime());
        reservation.setStatus(reservationDetails.getStatus());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return ResponseEntity.ok(updatedReservation);
    }

    // Cliente: Cancelar su reserva
    @Operation(summary = "Cancelar la reserva del usuario actual")
    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMyReservation(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (!reservation.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tiene permiso para eliminar esta reserva.");
        }

        reservationRepository.delete(reservation);
        return ResponseEntity.noContent().build();
    }

    // Admin: Obtener todas las reservas
    @Operation(summary = "Obtener todas las reservas (solo para administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // Admin: Crear una reserva
    @Operation(summary = "Crear una reserva (solo para administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<?> createReservationAdmin(@Valid @RequestBody Reservation reservation) {
        // Validar que la habitación existe
        HotelRoom room = hotelRoomRepository.findById(reservation.getService().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada"));

        // Validar que la fecha de la reserva no sea en el pasado
        if (reservation.getReservationDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("La fecha de la reserva no puede ser en el pasado.");
        }

        // Validar si la habitación está disponible para la fecha solicitada
        List<Reservation> existingReservations = reservationRepository.findByServiceId(room.getId());
        for (Reservation existingReservation : existingReservations) {
            if (existingReservation.getReservationDateTime().equals(reservation.getReservationDateTime())) {
                return ResponseEntity.badRequest().body("La habitación ya está reservada para la fecha y hora seleccionada.");
            }
        }

        reservation.setService(room);
        Reservation savedReservation = reservationRepository.save(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    // Admin: Actualizar una reserva
    @Operation(summary = "Actualizar una reserva (solo para administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateReservationAdmin(@PathVariable Long id, @Valid @RequestBody Reservation reservationDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        // Validar que la nueva fecha de la reserva no sea en el pasado
        if (reservationDetails.getReservationDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("La nueva fecha de la reserva no puede ser en el pasado.");
        }

        // Validar si la habitación está disponible para la nueva fecha de la reserva
        List<Reservation> existingReservations = reservationRepository.findByServiceId(reservation.getService().getId());
        for (Reservation existingReservation : existingReservations) {
            if (!existingReservation.getId().equals(reservation.getId()) &&
                existingReservation.getReservationDateTime().equals(reservationDetails.getReservationDateTime())) {
                return ResponseEntity.badRequest().body("La habitación ya está reservada para la nueva fecha y hora seleccionada.");
            }
        }

        // Actualizar los detalles de la reserva
        reservation.setReservationDateTime(reservationDetails.getReservationDateTime());
        reservation.setStatus(reservationDetails.getStatus());
        reservation.setService(reservationDetails.getService());
        reservation.setUser(reservationDetails.getUser());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return ResponseEntity.ok(updatedReservation);
    }

    // Admin: Eliminar una reserva
    @Operation(summary = "Eliminar una reserva (solo para administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteReservationAdmin(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        reservationRepository.delete(reservation);
        return ResponseEntity.noContent().build();
    }
}