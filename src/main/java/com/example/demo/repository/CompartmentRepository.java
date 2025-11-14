package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Compartment;

public interface CompartmentRepository extends JpaRepository<Compartment, Long> {
    
}
