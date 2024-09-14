package com.fernandomontealegre.reservationsystem.reservationsystem.repository;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Métodos personalizados, si es necesario
    Customer findByEmail(String email);
}