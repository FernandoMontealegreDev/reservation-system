package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

import com.fernandomontealegre.reservationsystem.reservationsystem.dto.ReservationRequest;
import com.fernandomontealegre.reservationsystem.reservationsystem.model.*;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
class ReservationControllerTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private HotelRoomRepository hotelRoomRepository;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        // Inicializa los mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReservation_ShouldReturnCreatedReservation() {
        // Crear un objeto de solicitud de reserva
        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setServiceId(1L);
        reservationRequest.setReservationDateTime(LocalDateTime.now().plusDays(1));

        // Set up mock objects and data
        User mockUser = new User();
        mockUser.setId(1L);

        HotelRoom mockRoom = new HotelRoom();
        mockRoom.setId(1L);

        Reservation mockReservation = new Reservation();
        mockReservation.setId(1L);
        mockReservation.setReservationDateTime(LocalDateTime.now().plusDays(1));
        mockReservation.setService(mockRoom);
        mockReservation.setUser(mockUser);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(hotelRoomRepository.findById(1L)).thenReturn(Optional.of(mockRoom));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(mockReservation);

        // Llamar al método a probar
        ResponseEntity<?> response = reservationController.createReservation(reservationRequest);

        // Validar la respuesta
        assertEquals(201, response.getStatusCode().value());

        // Verificar que los métodos mockeados fueron llamados
        verify(hotelRoomRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}