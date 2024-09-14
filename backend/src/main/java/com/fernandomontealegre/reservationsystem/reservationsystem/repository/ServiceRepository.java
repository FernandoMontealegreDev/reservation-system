package com.fernandomontealegre.reservationsystem.reservationsystem.repository;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
}