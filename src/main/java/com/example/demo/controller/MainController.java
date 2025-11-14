package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.demo.model.Compartment;
import com.example.demo.model.HistoricalMovement;
import com.example.demo.model.Ingredient;
import com.example.demo.model.IngredientType;
import com.example.demo.service.CompartmentService;
import com.example.demo.service.HistoricalMovementService;
import com.example.demo.service.IngredientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MainController {

    private final IngredientService ingredientService;
    private final CompartmentService compartmentService;
    private final HistoricalMovementService historicalService;

    public MainController(
            IngredientService ingredientService,
            CompartmentService compartmentService,
            HistoricalMovementService historicalService
    ) {
        this.ingredientService = ingredientService;
        this.compartmentService = compartmentService;
        this.historicalService = historicalService;
    }

   // ---- Compartimentos ----
    @Tag(name = "Compartimentos", description = "Gerenciamento dos compartimentos do armazém")
    @Operation(
        summary = "Cadastra um novo compartimento",
        description = "Cria um compartimento, valida regras do compartimento e registra histórico automaticamente."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compartimento criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação das regras de negócio")
    })
    
    @PostMapping("/compartimentos")
    public Compartment createCompartment(@RequestBody Compartment compartment) {
        return compartmentService.salvar(compartment);
    }

    @GetMapping("/compartimentos")
    public List<Compartment> getAllCompartments() {
        return compartmentService.listarTodos();
    }

    // Compartimentos disponíveis para armazenar
    @GetMapping("/compartimentos/disponiveis")
    public List<Compartment> getCompartimentosDisponiveis(
            @RequestParam double quantidade,
            @RequestParam IngredientType tipo
    ) {
        return compartmentService.getCompartimentosDisponiveis(quantidade, tipo);
    }

    // Compartimentos com volume > 0
    @GetMapping("/compartimentos/disponiveis-para-venda")
    public List<Compartment> getDisponiveisParaVenda(
            @RequestParam IngredientType tipo
    ) {
        return compartmentService.getDisponiveisParaVenda(tipo);
    }


    // ---- Ingredientes ----
    @Tag(name = "Ingredientes", description = "Gerenciamento dos ingredientes do armazém")
    @Operation(
        summary = "Cadastra um novo ingrediente",
        description = "Cria um ingrediente, valida regras do compartimento e registra histórico automaticamente."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ingrediente criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro de validação das regras de negócio")
    })

    // Criar ingrediente
    @PostMapping("/ingredientes/{compartimentoId}")
    public Ingredient createIngredient(
            @Parameter(description = "ID do compartimento onde o ingrediente será armazenado")
            @PathVariable Long compartimentoId,

            @Parameter(description = "Objeto contendo tipo, nome, quantidade e responsável")
            @RequestBody Ingredient ingredient
    ) {
        return ingredientService.criarIngrediente(compartimentoId, ingredient);
    }

    // Listar todos ingredientes
    @GetMapping("/ingredientes")
    public List<Ingredient> getAllIngredients() {
        return ingredientService.listarTodos();
    }

    @Operation(
        summary = "Remove um ingrediente",
        description = "Gera uma movimentação de saída e atualiza o estoque."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ingrediente removido com sucesso"),
        @ApiResponse(responseCode = "400", description = "Responsável ausente ou ingrediente inexistente")
    })

    // Remover ingrediente
    @DeleteMapping("/ingredientes/{id}")
    public void deleteIngredient(
        @Parameter(description = "ID do ingrediente a ser removido")
        @PathVariable Long id,

        @Parameter(description = "Nome da pessoa responsável pela remoção")
        @RequestParam(required = false) String responsavel
    ) {
        ingredientService.removerIngrediente(id, responsavel);
    }

    // Volume total por tipo
    @GetMapping("/ingredientes/volume")
    public Map<String, Double> getVolumeByType() {
        return ingredientService.calcularVolumePorTipo();
    }


    // ---- Histórico ----
    @Tag(name = "Histórico", description = "Gerenciamento do histórico de movimentações")
    @Operation(
        summary = "Consulta histórico",
        description = "Retorna uma lista de todas as movimentações registradas no sistema."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao buscar histórico")
    })
    @GetMapping("/historico")
    public List<HistoricalMovement> getAllHistoricalMovements() {
        return historicalService.listarTodos();
    }

    // histórico ordenado
    @Operation(
        summary = "Consulta histórico ordenado",
        description = "Permite ordenar por data, quantidade ou nome do compartimento."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Histórico ordenado retornado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros de ordenação inválidos")
    })
    @GetMapping("/historico-ordenado")
    public List<HistoricalMovement> getHistoricoOrdenado(
        @Parameter(description = "Campo de ordenação: data_hora, quantidade, compartimento")
        @RequestParam(defaultValue = "data_hora") String sortBy,

        @Parameter(description = "Ordem: asc ou desc")
        @RequestParam(defaultValue = "asc") String order
    ) {
        return historicalService.ordenarHistorico(sortBy, order);
    }
}
