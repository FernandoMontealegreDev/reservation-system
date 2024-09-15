package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

// Importaciones relacionadas con el modelo y repositorio
import com.fernandomontealegre.reservationsystem.reservationsystem.model.HotelRoom;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.HotelRoomRepository;

// Importaciones de Spring Framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// Importaciones de Jakarta Validation y Java
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/hotel-rooms")
public class HotelRoomController {

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    // Todos los usuarios pueden ver las habitaciones
    @GetMapping
    public List<HotelRoom> getAllHotelRooms() {
        return hotelRoomRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelRoom> getHotelRoomById(@PathVariable Long id) {
        return hotelRoomRepository.findById(id)
                .map(room -> ResponseEntity.ok().body(room))
                .orElse(ResponseEntity.notFound().build());
    }

    // Solo admins pueden crear, actualizar y eliminar habitaciones
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<HotelRoom> createHotelRoom(@Valid @RequestBody HotelRoom hotelRoom) {
        HotelRoom savedHotelRoom = hotelRoomRepository.save(hotelRoom);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHotelRoom);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<HotelRoom> updateHotelRoom(@PathVariable Long id, @Valid @RequestBody HotelRoom hotelRoomDetails) {
        return hotelRoomRepository.findById(id)
                .map(hotelRoom -> {
                    hotelRoom.setRoomNumber(hotelRoomDetails.getRoomNumber());
                    hotelRoom.setDescription(hotelRoomDetails.getDescription());
                    hotelRoom.setPrice(hotelRoomDetails.getPrice());
                    hotelRoom.setRoomType(hotelRoomDetails.getRoomType());
                    HotelRoom updatedHotelRoom = hotelRoomRepository.save(hotelRoom);
                    return ResponseEntity.ok().body(updatedHotelRoom);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotelRoom(@PathVariable Long id) {
        Optional<HotelRoom> optionalHotelRoom = hotelRoomRepository.findById(id);
        if (optionalHotelRoom.isPresent()) {
            hotelRoomRepository.delete(optionalHotelRoom.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}