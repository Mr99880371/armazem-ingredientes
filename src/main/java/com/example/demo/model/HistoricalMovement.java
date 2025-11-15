package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "historico_movimentacao")
public class HistoricalMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MovementType operacao;  // ENTRADA ou SAIDA

    private double quantidade;

    // guardamos o tipo do ingrediente como texto (independente do Ingredient FK)
    private String tipoIngrediente;

    // guardamos o nome do ingrediente (não referenciamos a entidade Ingredient)
    private String nomeIngrediente;

    // responsável pela movimentação (pode ser null/empty se não informado)
    private String responsavel;

    // data e hora da movimentação
    private LocalDateTime dataHora = LocalDateTime.now();

    // compartimento onde o ingrediente foi movido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compartimento_id")
    private Compartment compartimento;

    public HistoricalMovement(MovementType operacao, double quantidade, String tipoIngrediente,
                              String nomeIngrediente, String responsavel, Compartment compartimento) {
        this.operacao = operacao;
        this.quantidade = quantidade;
        this.tipoIngrediente = tipoIngrediente;
        this.nomeIngrediente = nomeIngrediente;
        this.responsavel = responsavel;
        this.compartimento = compartimento;
        this.dataHora = LocalDateTime.now();
    }
}
