package com.fernandomontealegre.reservationsystem.reservationsystem.controller;

import com.fernandomontealegre.reservationsystem.reservationsystem.model.Service;
import com.fernandomontealegre.reservationsystem.reservationsystem.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        return serviceRepository.findById(id)
                .map(service -> ResponseEntity.ok().body(service))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        Service savedService = serviceRepository.save(service);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedService);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @RequestBody Service serviceDetails) {
        return serviceRepository.findById(id)
                .map(service -> {
                    service.setName(serviceDetails.getName());
                    service.setDescription(serviceDetails.getDescription());
                    service.setPrice(serviceDetails.getPrice());
                    Service updatedService = serviceRepository.save(service);
                    return ResponseEntity.ok().body(updatedService);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        return serviceRepository.findById(id)
                .map(service -> {
                    serviceRepository.delete(service);
                    return ResponseEntity.noContent().<Void>build(); // Cambiado para especificar el tipo
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
