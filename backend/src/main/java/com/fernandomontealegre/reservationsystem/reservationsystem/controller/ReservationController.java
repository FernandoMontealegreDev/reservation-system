package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.Customer;
import com.fernandomontealegre.reservationsystem.reservationsystem.model.Reservation;
import com.fernandomontealegre.reservationsystem.reservationsystem.model.Service;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.CustomerRepository;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.ReservationRepository;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.ServiceRepository;

import jakarta.validation.Valid;

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

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceRepository serviceRepository;

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
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody Reservation reservation) {
        Reservation savedReservation = reservationRepository.save(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
    }

@PutMapping("/{id}")
public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @Valid @RequestBody Reservation reservationDetails) {
    return reservationRepository.findById(id)
            .map(reservation -> {
                reservation.setReservationDateTime(reservationDetails.getReservationDateTime());
                reservation.setStatus(reservationDetails.getStatus());

                // Validar y obtener IDs de Customer y Service
                if (reservationDetails.getCustomer() == null || reservationDetails.getCustomer().getId() == null) {
                    throw new RuntimeException("Customer ID is missing in the request.");
                }
                if (reservationDetails.getService() == null || reservationDetails.getService().getId() == null) {
                    throw new RuntimeException("Service ID is missing in the request.");
                }

                Long customerId = reservationDetails.getCustomer().getId();
                Long serviceId = reservationDetails.getService().getId();

                // Buscar y asignar Customer y Service por sus IDs
                Customer customer = customerRepository.findById(customerId)
                        .orElseThrow(() -> new RuntimeException("Customer not found with id " + customerId));

                Service service = serviceRepository.findById(serviceId)
                        .orElseThrow(() -> new RuntimeException("Service not found with id " + serviceId));

                reservation.setCustomer(customer);
                reservation.setService(service);

                // Guardar la reserva actualizada
                Reservation updatedReservation = reservationRepository.save(reservation);
                return ResponseEntity.ok().body(updatedReservation);
            })
            .orElseThrow(() -> new RuntimeException("Reservation not found with id " + id));
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservationRepository.delete(reservation);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}