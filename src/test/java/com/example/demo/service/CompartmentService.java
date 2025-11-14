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
    public CompartmentRepository compartmentRepository;

    @Autowired
    public IngredientRepository ingredientRepository;

    @Autowired
    public HistoricalMovementRepository historicalMovementRepository;

    // Construtor para testes unitários
    public CompartmentService() {}

    public CompartmentService(
            CompartmentRepository cRepo,
            IngredientRepository iRepo,
            HistoricalMovementRepository hRepo
    ) {
        this.compartmentRepository = cRepo;
        this.ingredientRepository = iRepo;
        this.historicalMovementRepository = hRepo;
    }

    // Inferir tipo pela capacidade
    private IngredientType inferirTipoPelaCapacidade(double capacidade) {
        if (capacidade == 600) return IngredientType.SECOS;
        if (capacidade == 500) return IngredientType.LIQUIDOS;
        if (capacidade == 400) return IngredientType.REFRIGERADOS;

        throw new BadRequestException("Capacidade inválida.");
    }

    public void validarCompatibilidadeDeTipo(Compartment compartimento, IngredientType tipoRecebido) {
        IngredientType tipoPermitido = inferirTipoPelaCapacidade(compartimento.getCapacidadeMaxima());

        if (tipoPermitido != tipoRecebido) {
            throw new BadRequestException(
                    "Tipo incompatível. Este compartimento só aceita: " + tipoPermitido
            );
        }
    }

    // CRUD
    public Compartment salvar(Compartment c) {
        return compartmentRepository.save(c);
    }

    public List<Compartment> listarTodos() {
        return compartmentRepository.findAll();
    }

    public Compartment getCompartimento(Long id) {
        return compartmentRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Compartimento não encontrado."));
    }

    // Mudança de tipo
    public void validarMudancaDeTipo(Compartment c, IngredientType novoTipo) {

        if (c.getTipo() == null) {
            c.setTipo(novoTipo);
            c.setDataUltimaTrocaTipo(LocalDate.now());
            return;
        }

        if (c.getTipo() == novoTipo) return;

        if (c.getQuantidadeAtual() > 0)
            throw new BadRequestException(
                    "Ainda contém ingredientes do tipo " + c.getTipo()
            );

        if (c.getDataUltimaTrocaTipo().isEqual(LocalDate.now()))
            throw new BadRequestException("Troca de tipo só amanhã.");

        c.setTipo(novoTipo);
        c.setDataUltimaTrocaTipo(LocalDate.now());
    }

    // Capacidade
    public void validarCapacidade(Compartment c, double qtd) {
        double nova = c.getQuantidadeAtual() + qtd;

        if (nova > c.getCapacidadeMaxima())
            throw new BadRequestException("Capacidade excedida.");
    }

    public void atualizarQuantidade(Compartment c, double qtd) {
        c.setQuantidadeAtual(c.getQuantidadeAtual() + qtd);
        compartmentRepository.save(c);
    }

    public void retirarQuantidade(Compartment c, double qtd) {
        double nova = c.getQuantidadeAtual() - qtd;
        if (nova < 0) nova = 0;
        c.setQuantidadeAtual(nova);
        compartmentRepository.save(c);
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
            case "quantidade" ->
                    comparator = Comparator.comparing(HistoricalMovement::getQuantidade);

            case "compartimento" ->
                    comparator = Comparator.comparing(
                            m -> m.getCompartimento() != null ? m.getCompartimento().getNome() : ""
                    );

            default ->
                    comparator = Comparator.comparing(HistoricalMovement::getDataHora);
        }

        if (order.equalsIgnoreCase("desc")) comparator = comparator.reversed();

        return lista.stream().sorted(comparator).toList();
    }
}
