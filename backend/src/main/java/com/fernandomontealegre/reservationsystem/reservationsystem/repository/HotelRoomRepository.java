package com.fernandomontealegre.reservationsystem.reservationsystem.repository;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.HotelRoom;

import org.springframework.data.jpa.repository.JpaRepository;


public interface HotelRoomRepository extends JpaRepository<HotelRoom, Long> {
}