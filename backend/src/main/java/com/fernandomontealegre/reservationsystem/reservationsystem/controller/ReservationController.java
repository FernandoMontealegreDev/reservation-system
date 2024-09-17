package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

import com.fernandomontealegre.reservationsystem.reservationsystem.dto.*;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation Controller", description = "Endpoints para la gestión de reservas de habitaciones")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Autowired
    private UserRepository userRepository;

    // Cliente: Crear una reserva
    @Operation(summary = "Crear una nueva reserva")
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequest reservationRequest) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Validar que la habitación existe
        HotelRoom room = hotelRoomRepository.findById(reservationRequest.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada"));

        // Validar que la fecha de la reserva no sea en el pasado
        if (reservationRequest.getReservationDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "La fecha de la reserva no puede ser en el pasado."));
        }

        // Validar si la habitación está disponible para la fecha solicitada
        boolean isAvailable = reservationRepository.isRoomAvailable(room.getId(), reservationRequest.getReservationDateTime());
        if (!isAvailable) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "La habitación ya está reservada para la fecha y hora seleccionada."));
        }

        // Crear la reserva
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setService(room);
        reservation.setReservationDateTime(reservationRequest.getReservationDateTime());
        reservation.setStatus(Reservation.ReservationStatus.valueOf("PENDING"));

        Reservation savedReservation = reservationRepository.save(reservation);

        // Convertir a ReservationResponse
        ReservationResponse reservationResponse = convertToResponse(savedReservation);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, reservationResponse, "Reserva creada exitosamente"));
    }

    // Cliente: Ver sus propias reservas
    @Operation(summary = "Ver las reservas del usuario actual")
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/my")
    public ResponseEntity<?> getMyReservations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<Reservation> reservations = reservationRepository.findByUserId(user.getId());

        List<ReservationResponse> reservationResponses = reservations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, reservationResponses, "Reservas obtenidas exitosamente"));
    }

    // Cliente: Modificar su reserva
    @Operation(summary = "Actualizar la reserva del usuario actual")
    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMyReservation(@PathVariable Long id, @Valid @RequestBody ReservationUpdateRequest reservationUpdateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (!reservation.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, "No tiene permiso para modificar esta reserva."));
        }

        // Validar que la nueva fecha de la reserva no sea en el pasado
        if (reservationUpdateRequest.getReservationDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "La nueva fecha de la reserva no puede ser en el pasado."));
        }

        // Validar si la habitación está disponible para la nueva fecha de la reserva
        boolean isAvailable = reservationRepository.isRoomAvailableForUpdate(reservation.getService().getId(), reservationUpdateRequest.getReservationDateTime(), reservation.getId());
        if (!isAvailable) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "La habitación ya está reservada para la nueva fecha y hora seleccionada."));
        }

        // Actualizar los detalles de la reserva
        reservation.setReservationDateTime(reservationUpdateRequest.getReservationDateTime());
        reservation.setStatus(Reservation.ReservationStatus.valueOf(reservationUpdateRequest.getStatus()));

        Reservation updatedReservation = reservationRepository.save(reservation);
        ReservationResponse reservationResponse = convertToResponse(updatedReservation);
        return ResponseEntity.ok(new ApiResponse<>(true, reservationResponse, "Reserva actualizada exitosamente"));
    }

    // Cliente: Cancelar su reserva
    @Operation(summary = "Cancelar la reserva del usuario actual")
    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMyReservation(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (!reservation.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, "No tiene permiso para eliminar esta reserva."));
        }

        reservationRepository.delete(reservation);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Reserva eliminada exitosamente"));
    }

    // Admin: Obtener todas las reservas
    @Operation(summary = "Obtener todas las reservas (solo para administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        List<ReservationResponse> reservationResponses = reservations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, reservationResponses, "Reservas obtenidas exitosamente"));
    }

    // Admin: Crear una reserva
    @Operation(summary = "Crear una reserva (solo para administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<?> createReservationAdmin(@Valid @RequestBody AdminReservationRequest adminReservationRequest) {
        // Validar que la habitación existe
        HotelRoom room = hotelRoomRepository.findById(adminReservationRequest.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada"));

        // Validar que el usuario existe
        User user = userRepository.findById(adminReservationRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Validar que la fecha de la reserva no sea en el pasado
        if (adminReservationRequest.getReservationDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "La fecha de la reserva no puede ser en el pasado."));
        }

        // Validar si la habitación está disponible para la fecha solicitada
        boolean isAvailable = reservationRepository.isRoomAvailable(room.getId(), adminReservationRequest.getReservationDateTime());
        if (!isAvailable) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "La habitación ya está reservada para la fecha y hora seleccionada."));
        }

        // Crear la reserva
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setService(room);
        reservation.setReservationDateTime(adminReservationRequest.getReservationDateTime());
        reservation.setStatus(Reservation.ReservationStatus.valueOf(adminReservationRequest.getStatus().toUpperCase()));


        Reservation savedReservation = reservationRepository.save(reservation);
        ReservationResponse reservationResponse = convertToResponse(savedReservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, reservationResponse, "Reserva creada exitosamente"));
    }

    // Admin: Actualizar una reserva
    @Operation(summary = "Actualizar una reserva (solo para administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateReservationAdmin(@PathVariable Long id, @Valid @RequestBody AdminReservationUpdateRequest adminReservationUpdateRequest) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        // Validar que la habitación existe
        HotelRoom room = hotelRoomRepository.findById(adminReservationUpdateRequest.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada"));

        // Validar que el usuario existe
        User user = userRepository.findById(adminReservationUpdateRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Validar que la nueva fecha de la reserva no sea en el pasado
        if (adminReservationUpdateRequest.getReservationDateTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "La nueva fecha de la reserva no puede ser en el pasado."));
        }

        // Validar si la habitación está disponible para la nueva fecha de la reserva
        boolean isAvailable = reservationRepository.isRoomAvailableForUpdate(room.getId(), adminReservationUpdateRequest.getReservationDateTime(), reservation.getId());
        if (!isAvailable) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "La habitación ya está reservada para la nueva fecha y hora seleccionada."));
        }

        // Actualizar los detalles de la reserva
        reservation.setReservationDateTime(adminReservationUpdateRequest.getReservationDateTime());
        reservation.setStatus(Reservation.ReservationStatus.valueOf(adminReservationUpdateRequest.getStatus().toUpperCase()));

        reservation.setService(room);
        reservation.setUser(user);

        Reservation updatedReservation = reservationRepository.save(reservation);
        ReservationResponse reservationResponse = convertToResponse(updatedReservation);
        return ResponseEntity.ok(new ApiResponse<>(true, reservationResponse, "Reserva actualizada exitosamente"));
    }

    // Admin: Eliminar una reserva
    @Operation(summary = "Eliminar una reserva (solo para administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteReservationAdmin(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        reservationRepository.delete(reservation);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Reserva eliminada exitosamente"));
    }

    // Método auxiliar para convertir una entidad Reservation a ReservationResponse
    private ReservationResponse convertToResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getService(),
                reservation.getReservationDateTime(),
                reservation.getStatus()
        );
    }
}