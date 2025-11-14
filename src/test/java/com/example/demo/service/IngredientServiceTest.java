package com.example.demo.service;

import com.example.demo.exceptions.BadRequestException;
import com.example.demo.model.Compartment;
import com.example.demo.model.HistoricalMovement;
import com.example.demo.model.Ingredient;
import com.example.demo.model.IngredientType;
import com.example.demo.model.MovementType;
import com.example.demo.repository.HistoricalMovementRepository;
import com.example.demo.repository.IngredientRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IngredientServiceTest {

    private IngredientRepository ingredientRepository;
    private HistoricalMovementRepository historicoRepository;
    private CompartmentService compartmentService;
    private IngredientService ingredientService;

    // Setup
    @BeforeEach
    void setup() {
        ingredientRepository = mock(IngredientRepository.class);
        historicoRepository = mock(HistoricalMovementRepository.class);
        compartmentService = mock(CompartmentService.class);

        ingredientService = new IngredientService(
                ingredientRepository,
                historicoRepository,
                compartmentService
        );
    }

    // Testes
    @Test
    void deveCriarIngredienteComSucesso() {
        Compartment c = new Compartment();
        c.setId(1L);
        c.setCapacidadeMaxima(600.0);
        c.setQuantidadeAtual(0.0);
        c.setTipo(IngredientType.SECOS);

        Ingredient ing = new Ingredient();
        ing.setNome("Farinha");
        ing.setQuantidade(50);
        ing.setResponsavel("Mariane");
        ing.setTipo(IngredientType.SECOS);

        when(compartmentService.getCompartimento(1L)).thenReturn(c);
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ing);

        Ingredient resultado = ingredientService.criarIngrediente(1L, ing);

        assertNotNull(resultado);
        assertEquals("Farinha", resultado.getNome());
        verify(historicoRepository, times(1)).save(any(HistoricalMovement.class));
    }

    @Test
    void deveFalharSemResponsavel() {
        Ingredient ing = new Ingredient();
        ing.setTipo(IngredientType.SECOS);
        ing.setNome("Farinha");
        ing.setQuantidade(10);
        ing.setResponsavel("");

        Compartment c = new Compartment();
        when(compartmentService.getCompartimento(1L)).thenReturn(c);

        assertThrows(BadRequestException.class, () ->
                ingredientService.criarIngrediente(1L, ing));
    }

    @Test
    void deveFalharQuandoCapacidadeExcedida() {
        Compartment c = new Compartment();
        c.setTipo(IngredientType.SECOS);
        c.setCapacidadeMaxima(100);
        c.setQuantidadeAtual(90);

        Ingredient ing = new Ingredient();
        ing.setTipo(IngredientType.SECOS);
        ing.setQuantidade(20);
        ing.setNome("Açúcar");
        ing.setResponsavel("Mariane");

        when(compartmentService.getCompartimento(1L)).thenReturn(c);

        doThrow(new BadRequestException("Capacidade máxima excedida"))
                .when(compartmentService).validarCapacidade(c, 20);

        assertThrows(BadRequestException.class, () ->
                ingredientService.criarIngrediente(1L, ing));
    }

    @Test
    void deveRegistrarHistoricoAoCriarIngrediente() {
        Compartment c = new Compartment();
        c.setTipo(IngredientType.SECOS);
        c.setCapacidadeMaxima(600);

        Ingredient ing = new Ingredient();
        ing.setNome("Arroz");
        ing.setQuantidade(50);
        ing.setResponsavel("Ana");
        ing.setTipo(IngredientType.SECOS);

        when(compartmentService.getCompartimento(1L)).thenReturn(c);
        when(ingredientRepository.save(any())).thenReturn(ing);

        ingredientService.criarIngrediente(1L, ing);

        ArgumentCaptor<HistoricalMovement> captor = ArgumentCaptor.forClass(HistoricalMovement.class);
        verify(historicoRepository).save(captor.capture());

        assertEquals(MovementType.ENTRADA, captor.getValue().getOperacao());
        assertEquals(50, captor.getValue().getQuantidade());
    }

    @Test
    void deveFalharAoRemoverSemResponsavel() {
        assertThrows(BadRequestException.class, () ->
                ingredientService.removerIngrediente(1L, null));
    }

    @Test
    void deveRemoverIngredienteECriarHistorico() {
        Ingredient ing = new Ingredient();
        ing.setId(1L);
        ing.setNome("Feijão");
        ing.setQuantidade(30);
        ing.setResponsavel("Mariane");

        Compartment c = new Compartment();
        c.setTipo(IngredientType.SECOS);
        ing.setCompartimento(c);

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ing));

        ingredientService.removerIngrediente(1L, "Mariane");

        verify(historicoRepository, times(1)).save(any());
        verify(ingredientRepository, times(1)).delete(ing);
    }
}
