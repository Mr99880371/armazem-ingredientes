package com.example.demo.service;

import com.example.demo.model.Compartment;
import com.example.demo.model.HistoricalMovement;
import com.example.demo.model.MovementType;
import com.example.demo.repository.HistoricalMovementRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HistoricalMovementServiceTest {

    private HistoricalMovementRepository movementRepository;
    private HistoricalMovementService movementService;

    @BeforeEach
    void setup() {
        movementRepository = mock(HistoricalMovementRepository.class);
        movementService = new HistoricalMovementService(movementRepository);
    }

    // Listar todos os movimentos
    @Test
    void deveListarTodosMovimentos() {

        HistoricalMovement m1 = new HistoricalMovement(
                MovementType.ENTRADA,
                10,
                "SECOS",
                "Arroz",
                "Mariane",
                new Compartment()
        );

        HistoricalMovement m2 = new HistoricalMovement(
                MovementType.SAIDA,
                5,
                "SECOS",
                "Arroz",
                "João",
                new Compartment()
        );

        when(movementRepository.findAll()).thenReturn(Arrays.asList(m1, m2));

        List<HistoricalMovement> lista = movementService.listarTodos();

        assertEquals(2, lista.size());
        assertEquals(MovementType.ENTRADA, lista.get(0).getOperacao());
        assertEquals(MovementType.SAIDA, lista.get(1).getOperacao());
    }

    // Ordenar por quantidade
    @Test
    void deveOrdenarPorQuantidadeAscendente() {

        HistoricalMovement m1 = new HistoricalMovement(
                MovementType.ENTRADA, 50, "SECOS", "Feijão", "Ana", null
        );

        HistoricalMovement m2 = new HistoricalMovement(
                MovementType.ENTRADA, 10, "LIQUIDOS", "Suco", "Mariane", null
        );

        when(movementRepository.findAll()).thenReturn(Arrays.asList(m1, m2));

        List<HistoricalMovement> lista = movementService.ordenarHistorico("quantidade", "asc");

        assertEquals(10, lista.get(0).getQuantidade());
        assertEquals(50, lista.get(1).getQuantidade());
    }

    // Ordenar por quantidade descendente
    @Test
    void deveOrdenarPorQuantidadeDescendente() {

        HistoricalMovement m1 = new HistoricalMovement(
                MovementType.ENTRADA, 50, "SECOS", "Feijão", "Ana", null
        );

        HistoricalMovement m2 = new HistoricalMovement(
                MovementType.ENTRADA, 10, "LIQUIDOS", "Suco", "Mariane", null
        );

        when(movementRepository.findAll()).thenReturn(Arrays.asList(m1, m2));

        List<HistoricalMovement> lista = movementService.ordenarHistorico("quantidade", "desc");

        assertEquals(50, lista.get(0).getQuantidade());
        assertEquals(10, lista.get(1).getQuantidade());
    }

    // Ordenar por data/hora
    @Test
    void deveOrdenarPorDataHora() {

        HistoricalMovement m1 = new HistoricalMovement(
                MovementType.ENTRADA, 20, "SECOS", "Arroz", "Ana", null
        );
        m1.setDataHora(LocalDateTime.now().minusHours(2));

        HistoricalMovement m2 = new HistoricalMovement(
                MovementType.SAIDA, 5, "SECOS", "Arroz", "João", null
        );
        m2.setDataHora(LocalDateTime.now());

        when(movementRepository.findAll()).thenReturn(Arrays.asList(m2, m1));

        List<HistoricalMovement> lista = movementService.ordenarHistorico("data", "asc");

        assertEquals(m1, lista.get(0)); // mais antigo primeiro
        assertEquals(m2, lista.get(1)); // mais recente depois
    }

    // Ordenar por nome do compartimento
    @Test
    void deveOrdenarPorNomeDoCompartimento() {

        Compartment c1 = new Compartment();
        c1.setNome("A-01");

        Compartment c2 = new Compartment();
        c2.setNome("B-01");

        HistoricalMovement m1 = new HistoricalMovement(
                MovementType.ENTRADA, 10, "SECOS", "Feijão", "Ana", c1
        );

        HistoricalMovement m2 = new HistoricalMovement(
                MovementType.SAIDA, 10, "SECOS", "Feijão", "Ana", c2
        );

        when(movementRepository.findAll()).thenReturn(Arrays.asList(m2, m1));

        List<HistoricalMovement> lista = movementService.ordenarHistorico("compartimento", "asc");

        assertEquals("A-01", lista.get(0).getCompartimento().getNome());
        assertEquals("B-01", lista.get(1).getCompartimento().getNome());
    }
}

