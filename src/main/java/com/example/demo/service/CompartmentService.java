package com.example.demo.service;

import com.example.demo.exceptions.BadRequestException;
import com.example.demo.model.Compartment;
import com.example.demo.model.HistoricalMovement;
import com.example.demo.model.Ingredient;
import com.example.demo.model.IngredientType;
import com.example.demo.repository.CompartmentRepository;
import com.example.demo.repository.HistoricalMovementRepository;
import com.example.demo.repository.IngredientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompartmentService {

    @Autowired
    private CompartmentRepository compartmentRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private HistoricalMovementRepository historicalMovementRepository;

    // Construtor padrão – usado pelo Spring
    public CompartmentService() {}

    // Construtor para testes (Mock)
    public CompartmentService(
            CompartmentRepository compartmentRepository,
            IngredientRepository ingredientRepository,
            HistoricalMovementRepository historicalMovementRepository
    ) {
        this.compartmentRepository = compartmentRepository;
        this.ingredientRepository = ingredientRepository;
        this.historicalMovementRepository = historicalMovementRepository;
    }

    // Inferir tipo correto pela capacidade
    private IngredientType inferirTipoPelaCapacidade(double capacidade) {

        if (capacidade == 600) return IngredientType.SECOS;
        if (capacidade == 500) return IngredientType.LIQUIDOS;
        if (capacidade == 400) return IngredientType.REFRIGERADOS;

        throw new BadRequestException(
                "Capacidade inválida. As capacidades permitidas são: " +
                        "600 (SECOS), 500 (LÍQUIDOS), 400 (REFRIGERADOS)."
        );
    }

    // Validar compatibilidade tipo x capacidade
    public void validarCompatibilidadeDeTipo(Compartment compartimento, IngredientType tipoRecebido) {

        IngredientType tipoPermitido = inferirTipoPelaCapacidade(compartimento.getCapacidadeMaxima());

        if (tipoRecebido != tipoPermitido) {
            throw new BadRequestException(
                    "Tipo incompatível com este compartimento. " +
                            "Este compartimento suporta apenas ingredientes do tipo: " + tipoPermitido
            );
        }
    }

    // CRUD auxiliar
    public Compartment salvar(Compartment compartimento) {
        return compartmentRepository.save(compartimento);
    }

    public List<Compartment> listarTodos() {
        return compartmentRepository.findAll();
    }

    public Compartment getCompartimento(Long id) {
        return compartmentRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Compartimento não encontrado."));
    }

    // Regras — mudança de tipo
    public void validarMudancaDeTipo(Compartment compartimento, IngredientType novoTipo) {

        if (compartimento.getTipo() == null) {
            compartimento.setTipo(novoTipo);
            compartimento.setDataUltimaTrocaTipo(LocalDate.now());
            return;
        }

        if (compartimento.getTipo() == novoTipo) return;

        if (compartimento.getQuantidadeAtual() > 0) {
            throw new BadRequestException(
                    "Este compartimento ainda contém ingredientes do tipo "
                            + compartimento.getTipo() + ". Esvazie antes de trocar o tipo."
            );
        }

        if (compartimento.getDataUltimaTrocaTipo() != null &&
                compartimento.getDataUltimaTrocaTipo().isEqual(LocalDate.now())) {

            throw new BadRequestException("Não é permitido trocar o tipo no mesmo dia.");
        }

        compartimento.setTipo(novoTipo);
        compartimento.setDataUltimaTrocaTipo(LocalDate.now());
    }

    // Regras — capacidade
    public void validarCapacidade(Compartment compartimento, double qtd) {

        double nova = compartimento.getQuantidadeAtual() + qtd;

        if (nova > compartimento.getCapacidadeMaxima()) {
            throw new BadRequestException(
                    "Capacidade máxima excedida! Limite: " + compartimento.getCapacidadeMaxima() +
                            " | Tentativa: " + nova
            );
        }
    }

    public void atualizarQuantidade(Compartment compartimento, double qtd) {
        compartimento.setQuantidadeAtual(compartimento.getQuantidadeAtual() + qtd);
        compartmentRepository.save(compartimento);
    }

    public void retirarQuantidade(Compartment compartimento, double qtd) {

        double nova = compartimento.getQuantidadeAtual() - qtd;

        if (nova < 0) nova = 0;

        compartimento.setQuantidadeAtual(nova);
        compartmentRepository.save(compartimento);
    }

    // Relatórios
    public Map<String, Double> calcularVolumePorTipo() {
        List<Ingredient> ingredientes = ingredientRepository.findAll();

        Map<String, Double> total = new HashMap<>();

        for (Ingredient ing : ingredientes) {
            total.merge(ing.getTipo().name(), ing.getQuantidade(), Double::sum);
        }

        return total;
    }

    public List<Compartment> getCompartimentosDisponiveis(double quantidade, IngredientType tipo) {
        return compartmentRepository.findAll().stream()
                .filter(c -> {

                    boolean cabe = c.getQuantidadeAtual() + quantidade <= c.getCapacidadeMaxima();
                    if (!cabe) return false;

                    if (c.getTipo() == tipo) return true;
                    if (c.getTipo() == null) return true;
                    return c.getQuantidadeAtual() == 0;

                }).toList();
    }

    public List<Compartment> getDisponiveisParaVenda(IngredientType tipo) {
        return compartmentRepository.findAll().stream()
                .filter(c -> c.getTipo() == tipo)
                .filter(c -> c.getQuantidadeAtual() > 0)
                .toList();
    }

    public List<HistoricalMovement> ordenarHistorico(String sortBy, String order) {

        List<HistoricalMovement> lista = historicalMovementRepository.findAll();

        Comparator<HistoricalMovement> comparator;

        switch (sortBy) {
            case "quantidade":
                comparator = Comparator.comparing(HistoricalMovement::getQuantidade);
                break;

            case "compartimento":
                comparator = Comparator.comparing(
                        m -> m.getCompartimento() != null
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
