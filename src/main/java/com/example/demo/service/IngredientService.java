package com.example.demo.service;

import com.example.demo.exceptions.BadRequestException;
import com.example.demo.model.Compartment;
import com.example.demo.model.HistoricalMovement;
import com.example.demo.model.Ingredient;
import com.example.demo.model.MovementType;
import com.example.demo.repository.HistoricalMovementRepository;
import com.example.demo.repository.IngredientRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final HistoricalMovementRepository historicalMovementRepository;
    private final CompartmentService compartmentService;

    public IngredientService(
        IngredientRepository ingredientRepository,
        HistoricalMovementRepository historicalMovementRepository,
        CompartmentService compartmentService
    ) {
        this.ingredientRepository = ingredientRepository;
        this.historicalMovementRepository = historicalMovementRepository;
        this.compartmentService = compartmentService;
    }

    @Transactional // Garante que a operação seja feita em uma transação
    public Ingredient criarIngrediente(Long compartimentoId, Ingredient ingredient) {

        // Buscar compartimento
        Compartment compartimento = compartmentService.getCompartimento(compartimentoId);

        // Validações
        if (ingredient.getTipo() == null) {
            throw new BadRequestException("O tipo do ingrediente é obrigatório.");
        }

        if (ingredient.getQuantidade() <= 0) {
            throw new BadRequestException("A quantidade deve ser maior que zero.");
        }

        if (ingredient.getResponsavel() == null || ingredient.getResponsavel().isBlank()) {
            throw new BadRequestException("O responsável pela movimentação é obrigatório.");
        }

        if (ingredient.getNome() == null || ingredient.getNome().isBlank()) {
            throw new BadRequestException("O nome do ingrediente é obrigatório.");
        }

        // Regras
        compartmentService.validarMudancaDeTipo(compartimento, ingredient.getTipo());
        compartmentService.validarCapacidade(compartimento, ingredient.getQuantidade());

        // Atualizar estoque
        compartmentService.atualizarQuantidade(compartimento, ingredient.getQuantidade());

        // Vincular compartimento
        ingredient.setCompartimento(compartimento);

        // Salvar ingrediente
        Ingredient saved = ingredientRepository.save(ingredient);

        // Registrar histórico
        HistoricalMovement movimento = new HistoricalMovement(
            MovementType.ENTRADA,
            ingredient.getQuantidade(),
            compartimento.getTipo().name(),
            saved.getNome(),
            ingredient.getResponsavel(),
            compartimento
        );

        historicalMovementRepository.save(movimento);

        return saved;
    }

    @Transactional // Garante que a operação seja feita em uma transação
    public void removerIngrediente(Long id, String responsavel) {

        if (responsavel == null || responsavel.isBlank()) {
            throw new BadRequestException("O responsável pela remoção é obrigatório.");
        }

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Ingrediente não encontrado."));

        Compartment compartimento = ingredient.getCompartimento();

        compartmentService.retirarQuantidade(compartimento, ingredient.getQuantidade());

        // Registrar Saída
        HistoricalMovement movimento = new HistoricalMovement(
                MovementType.SAIDA,
                ingredient.getQuantidade(),
                compartimento.getTipo() != null ? compartimento.getTipo().name() : null,
                ingredient.getNome(),
                responsavel,
                compartimento
        );

        historicalMovementRepository.save(movimento);
        ingredientRepository.delete(ingredient);
    }

    public List<Ingredient> listarTodos() {
        return ingredientRepository.findAll();
    }

    public Map<String, Double> calcularVolumePorTipo() {
        return compartmentService.calcularVolumePorTipo();
    }

    public void validarIngredient(Ingredient ingredient) {
        throw new UnsupportedOperationException("Unimplemented method 'validarIngredient'");
    }

    public void validarResponsavel(Long id, String responsavel) {
        throw new UnsupportedOperationException("Unimplemented method 'validarResponsavel'");
    }
}
