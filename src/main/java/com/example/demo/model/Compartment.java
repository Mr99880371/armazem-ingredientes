package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "compartimentos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "ingredientes"})
public class Compartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version; // Versão do registro

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private IngredientType tipo; // Ex: SECOS, REFRIGERADOS, etc.

    @Column(nullable = false)
    private double capacidadeMaxima;

    @Column(nullable = false)
    private double quantidadeAtual;

    // Data da última troca de tipo
    @Column(name = "data_ultima_troca_tipo")
    private LocalDate dataUltimaTrocaTipo;

    // Relacionamento com Ingredient
    @OneToMany(mappedBy = "compartimento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("compartimento") // evita loop com Ingredient
    private List<Ingredient> ingredientes;

    // Relacionamento com HistoricalMovement
    @OneToMany(mappedBy = "compartimento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("compartimento") // evita loop com Ingredient
    private List<HistoricalMovement> historicoMovimentacao;

    // Construtor
    public Compartment(String nome, IngredientType tipo, double capacidadeMaxima, double quantidadeAtual, List<Ingredient> ingredientes, List<HistoricalMovement> historicoMovimentacao) {
        this.nome = nome;
        this.tipo = tipo;
        this.quantidadeAtual = quantidadeAtual;
        this.capacidadeMaxima = capacidadeMaxima;
        this.ingredientes = ingredientes;
        this.historicoMovimentacao = historicoMovimentacao;
    }

    // Método auxiliar opcional para associar ingredientes
    public void adicionarIngrediente(Ingredient ingrediente) {
        ingredientes.add(ingrediente);
        ingrediente.setCompartimento(this);
    }
}
