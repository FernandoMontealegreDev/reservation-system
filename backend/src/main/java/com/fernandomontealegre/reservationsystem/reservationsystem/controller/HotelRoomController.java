package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

import com.fernandomontealegre.reservationsystem.reservationsystem.dto.HotelRoomRequest;
import com.fernandomontealegre.reservationsystem.reservationsystem.dto.HotelRoomResponse;
import com.fernandomontealegre.reservationsystem.reservationsystem.dto.ApiResponse;
import com.fernandomontealegre.reservationsystem.reservationsystem.exception.ResourceNotFoundException;
import com.fernandomontealegre.reservationsystem.reservationsystem.model.HotelRoom;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.HotelRoomRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hotel-rooms")
@Tag(name = "Hotel Room Controller", description = "Endpoints para la gestión de habitaciones de hotel")
public class HotelRoomController {

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Operation(summary = "Obtener todas las habitaciones")
    @GetMapping
    public ResponseEntity<?> getAllHotelRooms() {
        List<HotelRoom> rooms = hotelRoomRepository.findAll();
        if (rooms.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        // Convertir entidades a DTOs
        List<HotelRoomResponse> roomResponses = rooms.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, roomResponses, "Habitaciones obtenidas exitosamente"));
    }

    @Operation(summary = "Obtener una habitación por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getHotelRoomById(@PathVariable Long id) {
        HotelRoom hotelRoom = hotelRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada con ID: " + id));
        HotelRoomResponse hotelRoomResponse = convertToResponse(hotelRoom);
        return ResponseEntity.ok(new ApiResponse<>(true, hotelRoomResponse, "Habitación obtenida exitosamente"));
    }

    @Operation(summary = "Crear una nueva habitación (solo para administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createHotelRoom(@Valid @RequestBody HotelRoomRequest hotelRoomRequest) {
        // Convertir DTO a entidad
        HotelRoom hotelRoom = new HotelRoom();
        hotelRoom.setRoomNumber(hotelRoomRequest.getRoomNumber());
        hotelRoom.setDescription(hotelRoomRequest.getDescription());
        hotelRoom.setPrice(hotelRoomRequest.getPrice());
        hotelRoom.setRoomType(hotelRoomRequest.getRoomType());

        HotelRoom savedHotelRoom = hotelRoomRepository.save(hotelRoom);
        HotelRoomResponse hotelRoomResponse = convertToResponse(savedHotelRoom);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, hotelRoomResponse, "Habitación creada exitosamente"));
    }

    @Operation(summary = "Actualizar una habitación (solo para administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHotelRoom(@PathVariable Long id, @Valid @RequestBody HotelRoomRequest hotelRoomRequest) {
        HotelRoom hotelRoom = hotelRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada con ID: " + id));

        hotelRoom.setRoomNumber(hotelRoomRequest.getRoomNumber());
        hotelRoom.setDescription(hotelRoomRequest.getDescription());
        hotelRoom.setPrice(hotelRoomRequest.getPrice());
        hotelRoom.setRoomType(hotelRoomRequest.getRoomType());

        HotelRoom updatedHotelRoom = hotelRoomRepository.save(hotelRoom);
        HotelRoomResponse hotelRoomResponse = convertToResponse(updatedHotelRoom);
        return ResponseEntity.ok(new ApiResponse<>(true, hotelRoomResponse, "Habitación actualizada exitosamente"));
    }

    @Operation(summary = "Eliminar una habitación (solo para administradores)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHotelRoom(@PathVariable Long id) {
        HotelRoom hotelRoom = hotelRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada con ID: " + id));

        hotelRoomRepository.delete(hotelRoom);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Habitación eliminada con éxito"));
    }

    // Método auxiliar para convertir una entidad a DTO
    private HotelRoomResponse convertToResponse(HotelRoom hotelRoom) {
        return new HotelRoomResponse(
                hotelRoom.getId(),
                hotelRoom.getRoomNumber(),
                hotelRoom.getDescription(),
                hotelRoom.getPrice(),
                hotelRoom.getRoomType()
        );
    }
}