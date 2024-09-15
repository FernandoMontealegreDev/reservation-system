package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

// Importaciones de excepciones, modelos y repositorios
import com.fernandomontealegre.reservationsystem.reservationsystem.exception.ResourceNotFoundException;
import com.fernandomontealegre.reservationsystem.reservationsystem.model.*;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.*;

// Importaciones de Spring Framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

// Importaciones de Jakarta Validation y Java
import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    // Cliente: Crear una reserva
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody Reservation reservation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        reservation.setUser(user);

        // Validar que la habitación existe
        HotelRoom room = hotelRoomRepository.findById(reservation.getService().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada"));

        reservation.setService(room);
        reservation.setStatus("PENDING");

        Reservation savedReservation = reservationRepository.save(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    // Cliente: Ver sus propias reservas
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/my")
    public ResponseEntity<?> getMyReservations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<Reservation> reservations = reservationRepository.findByUserId(user.getId());
        return ResponseEntity.ok(reservations);
    }

    // Cliente: Modificar su reserva
    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMyReservation(@PathVariable Long id, @Valid @RequestBody Reservation reservationDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (!reservation.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tiene permiso para modificar esta reserva");
        }

        // Actualizar los detalles de la reserva
        reservation.setReservationDateTime(reservationDetails.getReservationDateTime());
        reservation.setStatus(reservationDetails.getStatus());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return ResponseEntity.ok(updatedReservation);
    }

    // Cliente: Cancelar su reserva
    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMyReservation(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (!reservation.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No tiene permiso para eliminar esta reserva");
        }

        reservationRepository.delete(reservation);
        return ResponseEntity.noContent().build();
    }

    // Admin: Obtener todas las reservas
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    // Admin: Crear una reserva
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<?> createReservationAdmin(@Valid @RequestBody Reservation reservation) {
        // Validar que la habitación existe
        HotelRoom room = hotelRoomRepository.findById(reservation.getService().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada"));

        reservation.setService(room);
        Reservation savedReservation = reservationRepository.save(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    // Admin: Actualizar una reserva
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateReservationAdmin(@PathVariable Long id, @Valid @RequestBody Reservation reservationDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        // Actualizar los detalles de la reserva
        reservation.setReservationDateTime(reservationDetails.getReservationDateTime());
        reservation.setStatus(reservationDetails.getStatus());
        reservation.setService(reservationDetails.getService());
        reservation.setUser(reservationDetails.getUser());

        Reservation updatedReservation = reservationRepository.save(reservation);
        return ResponseEntity.ok(updatedReservation);
    }

    // Admin: Eliminar una reserva
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteReservationAdmin(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        reservationRepository.delete(reservation);
        return ResponseEntity.noContent().build();
    }
}