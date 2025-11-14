package com.example.demo.service;

import com.example.demo.model.HistoricalMovement;
import com.example.demo.repository.HistoricalMovementRepository;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class HistoricalMovementService {

    private final HistoricalMovementRepository historicalMovementRepository;

    public HistoricalMovementService(HistoricalMovementRepository historicalMovementRepository) {
        this.historicalMovementRepository = historicalMovementRepository;
    }

    // Salvar histórico (entrada / saída)
    public HistoricalMovement salvar(HistoricalMovement movimento) {
        return historicalMovementRepository.save(movimento);
    }

    // Listar todos os movimentos
    public List<HistoricalMovement> listarTodos() {
        return historicalMovementRepository.findAll();
    }

    // Ordenar histórico dinamicamente
    public List<HistoricalMovement> ordenarHistorico(String sortBy, String order) {

        List<HistoricalMovement> lista = historicalMovementRepository.findAll();

        Comparator<HistoricalMovement> comparator;

        switch (sortBy) {

            case "quantidade":
                comparator = Comparator.comparing(HistoricalMovement::getQuantidade);
                break;

            case "compartimento":
                comparator = Comparator.comparing(
                        m -> m.getCompartimento() != null && m.getCompartimento().getNome() != null
                                ? m.getCompartimento().getNome()
                                : ""
                );
                break;

            case "data":
            case "data_hora":
            default:
                comparator = Comparator.comparing(HistoricalMovement::getDataHora);
                break;
        }

        if (order.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }

        return lista.stream().sorted(comparator).toList();
    }
}
