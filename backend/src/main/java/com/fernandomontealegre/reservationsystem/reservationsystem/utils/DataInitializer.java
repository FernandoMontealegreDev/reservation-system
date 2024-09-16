package com.fernandomontealegre.reservationsystem.reservationsystem.utils;

// Importaciones de modelos y repositorios
import com.fernandomontealegre.reservationsystem.reservationsystem.model.*;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.*;

// Importaciones de Spring Framework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Importaciones de Java
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Inicializar usuario administrador si no existe
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("adminpassword"));
            adminUser.setName("Administrador");
            adminUser.setEmail("admin@example.com");
            adminUser.setRole(Role.ADMIN);

            userRepository.save(adminUser);
        }

        // Inicializar usuarios clientes si no existen
        List<User> clients = new ArrayList<>();
        if (userRepository.findByUsername("cliente1").isEmpty()) {
            User clientUser1 = new User();
            clientUser1.setUsername("cliente1");
            clientUser1.setPassword(passwordEncoder.encode("clientpassword1"));
            clientUser1.setName("Cliente Uno");
            clientUser1.setEmail("cliente1@example.com");
            clientUser1.setRole(Role.CLIENT);
            clients.add(clientUser1);
        }

        if (userRepository.findByUsername("cliente2").isEmpty()) {
            User clientUser2 = new User();
            clientUser2.setUsername("cliente2");
            clientUser2.setPassword(passwordEncoder.encode("clientpassword2"));
            clientUser2.setName("Cliente Dos");
            clientUser2.setEmail("cliente2@example.com");
            clientUser2.setRole(Role.CLIENT);
            clients.add(clientUser2);
        }

        if (!clients.isEmpty()) {
            userRepository.saveAll(clients);
        }

        // Inicializar habitaciones si no existen
        if (hotelRoomRepository.count() == 0) {
            HotelRoom room1 = new HotelRoom(null, "101", "Habitación sencilla con vista al mar", 100.0, RoomType.SINGLE);
            HotelRoom room2 = new HotelRoom(null, "102", "Habitación doble con balcón", 150.0, RoomType.DOUBLE);
            HotelRoom room3 = new HotelRoom(null, "201", "Suite presidencial", 300.0, RoomType.SUITE);
            HotelRoom room4 = new HotelRoom(null, "202", "Habitación familiar con dos camas dobles", 200.0, RoomType.DOUBLE);
            HotelRoom room5 = new HotelRoom(null, "301", "Habitación sencilla con vista al jardín", 120.0, RoomType.SINGLE);
            HotelRoom room6 = new HotelRoom(null, "302", "Suite con jacuzzi y terraza privada", 350.0, RoomType.SUITE);

            hotelRoomRepository.saveAll(Arrays.asList(room1, room2, room3, room4, room5, room6));
        }

        // Inicializar reservas si no existen
        if (reservationRepository.count() == 0 && !clients.isEmpty()) {
            List<HotelRoom> rooms = hotelRoomRepository.findAll();
            List<Reservation> reservations = new ArrayList<>();

            reservations.add(new Reservation(null, clients.get(0), rooms.get(0), LocalDateTime.now().plusDays(1), "CONFIRMED"));
            reservations.add(new Reservation(null, clients.get(0), rooms.get(1), LocalDateTime.now().plusDays(3), "PENDING"));
            reservations.add(new Reservation(null, clients.get(1), rooms.get(2), LocalDateTime.now().plusDays(2), "CONFIRMED"));
            reservations.add(new Reservation(null, clients.get(1), rooms.get(3), LocalDateTime.now().plusDays(5), "CANCELLED"));

            reservationRepository.saveAll(reservations);
        }
    }
}