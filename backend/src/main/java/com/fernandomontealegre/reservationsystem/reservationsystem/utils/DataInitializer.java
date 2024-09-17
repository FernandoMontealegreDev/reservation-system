package com.fernandomontealegre.reservationsystem.reservationsystem.utils;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.*;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeAdminUser();
        List<User> clients = initializeClientUsers();
        initializeHotelRooms();
        initializeReservations(clients);
    }

    private void initializeAdminUser() {
        userRepository.findByUsername("admin").ifPresentOrElse(
                user -> logger.info("Usuario administrador ya existe."),
                () -> {
                    User adminUser = new User();
                    adminUser.setUsername("admin");
                    adminUser.setPassword(passwordEncoder.encode("adminpassword"));
                    adminUser.setName("Administrador");
                    adminUser.setEmail("admin@example.com");
                    adminUser.setRole(RoleType.ADMIN);
                    userRepository.save(adminUser);
                    logger.info("Usuario administrador creado.");
                }
        );
    }

    private List<User> initializeClientUsers() {
        List<User> clients = new ArrayList<>();

        if (userRepository.findByUsername("cliente1").isEmpty()) {
            User clientUser1 = createClientUser("cliente1", "clientpassword1", "Cliente Uno", "cliente1@example.com");
            clients.add(clientUser1);
        }

        if (userRepository.findByUsername("cliente2").isEmpty()) {
            User clientUser2 = createClientUser("cliente2", "clientpassword2", "Cliente Dos", "cliente2@example.com");
            clients.add(clientUser2);
        }

        if (!clients.isEmpty()) {
            userRepository.saveAll(clients);
            logger.info("Usuarios clientes creados.");
        }

        return clients;
    }

    private User createClientUser(String username, String password, String name, String email) {
        User clientUser = new User();
        clientUser.setUsername(username);
        clientUser.setPassword(passwordEncoder.encode(password));
        clientUser.setName(name);
        clientUser.setEmail(email);
        clientUser.setRole(RoleType.CLIENT);
        return clientUser;
    }

    private void initializeHotelRooms() {
        if (hotelRoomRepository.count() == 0) {
            List<HotelRoom> rooms = Arrays.asList(
                    new HotelRoom(null, "101", "Habitación sencilla con vista al mar", 100.0, RoomType.SINGLE),
                    new HotelRoom(null, "102", "Habitación doble con balcón", 150.0, RoomType.DOUBLE),
                    new HotelRoom(null, "201", "Suite presidencial", 300.0, RoomType.SUITE),
                    new HotelRoom(null, "202", "Habitación familiar con dos camas dobles", 200.0, RoomType.DOUBLE),
                    new HotelRoom(null, "301", "Habitación sencilla con vista al jardín", 120.0, RoomType.SINGLE),
                    new HotelRoom(null, "302", "Suite con jacuzzi y terraza privada", 350.0, RoomType.SUITE)
            );

            hotelRoomRepository.saveAll(rooms);
            logger.info("Habitaciones de hotel creadas.");
        } else {
            logger.info("Las habitaciones de hotel ya existen.");
        }
    }

    private void initializeReservations(List<User> clients) {
        if (reservationRepository.count() == 0 && !clients.isEmpty()) {
            List<HotelRoom> rooms = hotelRoomRepository.findAll();
            List<Reservation> reservations = new ArrayList<>();

            reservations.add(new Reservation(null, clients.get(0), rooms.get(0), LocalDateTime.now().plusDays(1), Reservation.ReservationStatus.CONFIRMED));
            reservations.add(new Reservation(null, clients.get(0), rooms.get(1), LocalDateTime.now().plusDays(3), Reservation.ReservationStatus.PENDING));
            reservations.add(new Reservation(null, clients.get(1), rooms.get(2), LocalDateTime.now().plusDays(2), Reservation.ReservationStatus.CONFIRMED));
            reservations.add(new Reservation(null, clients.get(1), rooms.get(3), LocalDateTime.now().plusDays(5), Reservation.ReservationStatus.CANCELED));

            reservationRepository.saveAll(reservations);
            logger.info("Reservas creadas.");
        } else {
            logger.info("Las reservas ya existen o no hay clientes disponibles.");
        }
    }
}