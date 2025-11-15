# 📦 Armazém de Ingredientes e Compartimentos
⭐ Controle inteligente de estoque com regras fortes de consistência, histórico completo e validações robustas.

Este projeto foi desenvolvido com foco em boas práticas de arquitetura, separação clara entre Controller, Service, Repository, testes unitários, padronização de erros e documentação automática via Swagger.

O objetivo é simular um sistema real de armazenagem com:

- Controle de ingredientes

- Compartimentos classificáveis por tipo

- Regras rígidas de capacidade

- Compatibilidade entre tipo e compartimento

- Registro completo de entradas e saídas

- Histórico ordenável dinamicamente

- Testes unitários garantindo a integridade do domínio

- Documentação via Swagger/OpenAPI

### 🧱 Tecnologias Utilizadas

- Java 21+

- Spring Boot

- Spring Web

- Spring Data JPA

- H2 Database (modo file)

- Lombok

- OpenAPI/Swagger 3

- JUnit 5

- Mockito

### 📂 Arquitetura do Projeto
```text
src/main/java/com/example/demo

├── controller
│   └── MainController.java
│
├── service
│   ├── CompartmentService.java
│   ├── IngredientService.java
│   └── HistoricalMovementService.java
│
├── model
│   ├── Ingredient.java
│   ├── Compartment.java
│   ├── HistoricalMovement.java
│   ├── MovementType.java
│   └── IngredientType.java
│
├── repository
│   ├── IngredientRepository.java
│   ├── CompartmentRepository.java
│   └── HistoricalMovementRepository.java
│
└── exceptions
    ├── BadRequestException.java
    └── GlobalExceptionHandler.java
```

## ⚙️ Regras de Negócio Implementadas
### 🧂 Ingredientes

✔ Deve possuir: nome, tipo, quantidade, responsável.

✔ Deve ser armazenado somente se houver espaço no compartimento.

✔ Seu tipo deve ser compatível com o tipo esperado pelo compartimento.

### 📦 Compartimentos

Cada compartimento possui capacidade fixa e definida:

Capacidade	/ Tipo Permitido

600	SECOS

500	LÍQUIDOS

400	REFRIGERADOS


#### Regras importantes:

✔ Se o compartimento não tem tipo, ele assume o tipo do primeiro ingrediente.

✔ Se já tem tipo:

❌ Não pode receber ingrediente de tipo diferente

❌ Não pode trocar de tipo se não estiver vazio

❌ Não pode trocar de tipo no mesmo dia

✔ Capacidade nunca pode ser excedida.

✔ Ao remover um ingrediente, o estoque do compartimento é atualizado.


## 🕑 Histórico de movimentações

Cada movimentação registra:

- Tipo da operação (ENTRADA ou SAÍDA)

- Quantidade

- Nome do ingrediente

- Tipo do ingrediente

- Responsável

- Compartimento

- Data/hora

E pode ser ordenado por:

- quantidade

- data_hora

- nome do compartimento

## 🔥 Validações e Erros Padronizados

Todas as falhas retornam:

{
  "message": "Mensagem clara do erro"
}


#### Graças ao arquivo:

GlobalExceptionHandler.java


Que captura e converte qualquer BadRequestException em:

HTTP 400 (Bad Request)

## 🔐 Concorrência — @Transactional + @Version

Este projeto protege operações críticas:

#### 💠 @Transactional nos Services

Garante que:

Entrada de ingrediente

Saída de ingrediente

Atualização de estoque

Registro de histórico

… aconteçam de forma atômica e consistente.

#### 💠 @Version em Compartment

Protege de race conditions, como:

duas entradas simultâneas no mesmo compartimento

remoção concorrente de estoque

Se duas transações tentarem alterar o mesmo compartimento:

→ A JPA dispara OptimisticLockingFailureException

→ Aplicação evita inconsistências no estoque

Isso eleva o projeto a um nível profissional de concorrência.

## 🧪 Testes Unitários Implementados

Os testes garantem a integridade das regras de negócio.

#### Rodar:  
```mvn test```

#### ✔ CompartmentServiceTest

- Mudança de tipo válida/inválida

- Validação de capacidade

- Atualização de quantidade

- Remoção com limite

- Compatibilidade tipo × capacidade

- Cálculo de volume por tipo

#### ✔ HistoricalMovementServiceTest

- Listar histórico

- Ordenar por quantidade

- Ordenar por data

- Ordenar por compartimento

#### ✔ IngredientServiceTest

- Criar ingrediente válido

- Validar obrigatoriedade dos campos

- Regras de tipo

- Regras de capacidade

- Registrar histórico

- Remover ingrediente

## 🌐 Swagger — Documentação Automática

A documentação dos endpoints é gerada automaticamente usando:

```springdoc-openapi-starter-webmvc-ui```

#### ✔ Como acessar:

👉 http://localhost:8080/swagger-ui.html

TAGS aplicadas para organização:

* Compartimentos

* Ingredientes

* Histórico

E cada endpoint possui:

- @Operation → descrição clara

- @ApiResponses → códigos HTTP documentados

- @Tag → agrupamento no Swagger UI

## 📡 Endpoints

#### 📦 Compartimentos

#### Criar compartimento

```POST /api/compartimentos```

#### Listar compartimentos

```GET /api/compartimentos```

#### Compartimentos disponíveis para armazenar

```GET /api/compartimentos/disponiveis?quantidade=50&tipo={tipoIngrediente}```

#### Compartimentos disponíveis para venda

```GET /api/compartimentos/disponiveis-para-venda?tipo={tipoIngrediente}```

### 🧂 Ingredientes

#### Criar ingrediente

 ```POST /api/ingredientes/{compartimentoId}```

#### Listar ingredientes

```GET /api/ingredientes```

#### Remover ingrediente

```DELETE /api/ingredientes/{id}?responsavel=Mariane```

### 📜 Histórico

#### Listar histórico

```GET /api/historico```

#### Ordenar histórico

```GET /api/historico-ordenado?sortBy=data&order=desc```

## ▶️ Como Rodar o Projeto

#### Clone o repositório

```git clone <repositorio>```


#### Rode o projeto

```mvn spring-boot:run```

### 📜 Acesse o Swagger
👉 http://localhost:8080/swagger-ui.html


### 📜 Acesse o console H2 (opcional)
👉 http://localhost:8080/h2-console

#### 📜 Configuração:

```JDBC URL: jdbc:h2:file:./data/meubanco```
```User: sa```

### 🧹 Pontos Fortes da Arquitetura

- Separação correta Controller → Service → Repository

- Regras encapsuladas 100% no domínio

- Testes unitários garantindo regressão zero

- Histórico rastreável e ordenável

- Swagger documentando totalmente a API

- Exceções personalizadas com mensagens claras

- Compatibilidade inteligente entre capacidade e tipo

- Código limpo, sem duplicações

### 🎯 Conclusão

#### Este projeto demonstra um sistema completo e robusto de controle de estoque com:

✔ Regras de negócio sólidas

✔ Validações claras

✔ Histórico confiável

✔ Arquitetura escalável

✔ Testes unitários

✔ Documentação automática




#### Desenvolvido por Mariane A. Justino.
