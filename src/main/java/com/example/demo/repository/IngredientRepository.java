package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    
}
