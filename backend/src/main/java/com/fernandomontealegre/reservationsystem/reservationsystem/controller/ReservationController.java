package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.Reservation;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> ResponseEntity.ok().body(reservation))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        Reservation savedReservation = reservationRepository.save(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody Reservation reservationDetails) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservation.setReservationDate(reservationDetails.getReservationDate());
                    reservation.setReservationTime(reservationDetails.getReservationTime());
                    reservation.setStatus(reservationDetails.getStatus());
                    reservation.setCustomer(reservationDetails.getCustomer());
                    reservation.setService(reservationDetails.getService());
                    Reservation updatedReservation = reservationRepository.save(reservation);
                    return ResponseEntity.ok().body(updatedReservation);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservationRepository.delete(reservation);
                    return ResponseEntity.noContent().<Void>build(); // Corrección del tipo
                })
                .orElse(ResponseEntity.notFound().build());
    }
}