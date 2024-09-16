package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.HotelRoom;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.HotelRoomRepository;
import com.fernandomontealegre.reservationsystem.reservationsystem.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/hotel-rooms")
@Tag(name = "Hotel Room Controller", description = "Endpoints para la gestión de habitaciones de hotel")
public class HotelRoomController {

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Operation(summary = "Obtener todas las habitaciones")
    @GetMapping
    public ResponseEntity<List<HotelRoom>> getAllHotelRooms() {
        List<HotelRoom> rooms = hotelRoomRepository.findAll();
        if (rooms.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "Obtener una habitación por ID")
    @GetMapping("/{id}")
    public ResponseEntity<HotelRoom> getHotelRoomById(@PathVariable Long id) {
        HotelRoom hotelRoom = hotelRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada con ID: " + id));
        return ResponseEntity.ok(hotelRoom);
    }

    @Operation(summary = "Crear una nueva habitación")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<HotelRoom> createHotelRoom(@Valid @RequestBody HotelRoom hotelRoom) {
        HotelRoom savedHotelRoom = hotelRoomRepository.save(hotelRoom);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHotelRoom);
    }

    @Operation(summary = "Actualizar una habitación")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<HotelRoom> updateHotelRoom(@PathVariable Long id, @Valid @RequestBody HotelRoom hotelRoomDetails) {
        HotelRoom hotelRoom = hotelRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada con ID: " + id));

        hotelRoom.setRoomNumber(hotelRoomDetails.getRoomNumber());
        hotelRoom.setDescription(hotelRoomDetails.getDescription());
        hotelRoom.setPrice(hotelRoomDetails.getPrice());
        hotelRoom.setRoomType(hotelRoomDetails.getRoomType());

        HotelRoom updatedHotelRoom = hotelRoomRepository.save(hotelRoom);
        return ResponseEntity.ok(updatedHotelRoom);
    }

    @Operation(summary = "Eliminar una habitación")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHotelRoom(@PathVariable Long id) {
        HotelRoom hotelRoom = hotelRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada con ID: " + id));

        hotelRoomRepository.delete(hotelRoom);
        return ResponseEntity.ok("Habitación eliminada con éxito");
    }
}