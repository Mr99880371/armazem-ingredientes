package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.HistoricalMovement;

public interface HistoricalMovementRepository extends JpaRepository<HistoricalMovement, Long> {
    
}
