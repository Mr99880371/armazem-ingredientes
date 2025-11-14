package com.example.demo.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ingredientes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Chave primária auto-incrementada
    private Long id;

    private String nome; // Nome do ingrediente

    private double quantidade; // Quantidade do ingrediente (kg ou L)

    private String responsavel;

    @Enumerated(EnumType.STRING)
    private IngredientType tipo; // Tipo do ingrediente (SECOS, LIQUIDOS, REFRIGERADOS)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compartimento_id") // Chave estrangeira
    @JsonIgnoreProperties("ingredientes")  // Evita loop com Ingredient
    private Compartment compartimento; // Relacionamento com o Compartimento

    public Ingredient(String nome, double quantidade, Compartment compartimento, String responsavel) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.compartimento = compartimento;
        this.responsavel = responsavel;
    }
}
