# Tasks: Plano de Implementação do Order-Service

## [x] Fase 1: Domínio Core (Hexágono Interno com SOLID Puro)
- [x] Criar o Value Object `OrderStatus` com regras de transição explícitas.
- [x] Criar interfaces de portas de saída altamente granulares (Segregação de Interfaces - ISP):
  - [x] `OrderRepositoryPort` (Salvar e buscar pedidos)
  - [x] `CustomerClientPort` (Validar situação do cliente)
  - [x] `CatalogClientPort` (Validar estoque e buscar preço atual)
  - [x] `IdempotencyStoragePort` (Gerenciar chaves de idempotência)
- [x] Criar a entidade de domínio `Order` contendo lógica pura de encapsulamento de estado (métodos `addItem`, `confirmOrder`, `applyPaymentFailure`).
- [x] **Testes de Unidade (JUnit 5 + Mockito):** Desenvolver testes para a classe de domínio `Order` isolando todas as dependências por meio de mocks das portas.
- [x] Validar JaCoCo (mínimo 80%) e PITest (mínimo 75% de mutação) no escopo de domínio.

## [x] Fase 2: Segurança (JWT) e Adaptadores de Entrada (Inbound Adapters)
- [x] Configurar o Spring Security Resource Server para validar tokens JWT e aplicar restrições de escopo por endpoint.
- [x] Criar o `GlobalExceptionHandler` configurando respostas padronizadas via **RFC 7807** para tratar acessos negados e erros de negócio.
- [x] Criar os controladores REST (`OrderController`, `PaymentController`) injetando as interfaces de casos de uso (Inversão de Dependência - DIP).

## [x] Fase 3: Adaptadores de Saída (Outbound Adapters) e Persistência
- [x] Criar scripts de migração do **Flyway** estruturando as tabelas no MySQL.
- [x] Implementar `OrderRepositoryAdapter` utilizando o Spring Data JPA subjacente, mapeando o controle de concorrência com `@Version`.
- [x] Implementar os adaptadores HTTP de clientes usando Feign/WebClient protegidos por Circuit Breakers do Resilience4j para consumir o WireMock.

## [ ] Fase 4: Infraestrutura de Execução e Integração
- [ ] Configurar os arquivos JSON do WireMock mapeando as respostas de cliente e catálogo.
- [ ] Configurar os testes de integração Spring Boot com **Testcontainers** subindo MySQL e WireMock de forma isolada.
- [ ] Criar o `Dockerfile` multi-stage para Java 21 e o arquivo `docker-compose.yml`.

## [ ] Fase 5: Pipeline CI/CD e Segurança estática
- [ ] Criar a Action do GitHub em `.github/workflows/ci.yml`.
- [ ] Configurar a execução do build, testes unitários, testes de integração e o PITest.
- [ ] Configurar a etapa do **Trivy** para escanear a imagem Docker final e bloquear falhas críticas antes da conclusão da esteira.
