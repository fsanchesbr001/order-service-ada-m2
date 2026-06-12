# Constitution — Order Service

## 🏗️ 1. Arquitetura Hexagonal & Princípios SOLID
- **Inversão de Dependência (DIP):** O núcleo de domínio (`domain`) é 100% independente. Ele define os contratos de entrada e saída através de **Portas (Interfaces)**. Nenhuma classe de domínio pode instanciar adaptadores ou frameworks diretamente. Todo fluxo de dados externo entra via injeção de dependência das Portas.
- **Responsabilidade Única (SRP):** - Classes de domínio gerem apenas o estado interno e regras de negócio do agregado.
  - Cada caso de uso (`Use Case`) executa uma única operação de negócio.
  - Adaptadores isolam completamente o protocolo externo (ex: Spring MVC) da persistência (ex: Spring Data JPA).
- **Segregação de Interfaces (ISP):** As portas de saída devem ser granulares. Não criar uma interface única para todas as comunicações externas. Separar explicitamente em `OrderRepositoryPort`, `CustomerClientPort` e `CatalogClientPort`.
- **Aberto/Fechado (OCP):** O comportamento do sistema deve ser extensível sem alteração do núcleo. Regras como cálculo de taxas ou descontos devem ser abstraídas por interfaces de estratégia (*Strategy Pattern*), permitindo novos comportamentos sem modificar o caso de uso principal.

## 🛡️ 2. Segurança Absoluta (JWT & OWASP)
- **Autenticação e Autorização:** **Todos os endpoints, sem exceção**, exigem um token JWT válido enviado no cabeçalho `Authorization: Bearer <TOKEN>`.
- **RBAC (Role-Based Access Control):** A validação deve ser feita por escopos e permissões específicas extraídas do Token JWT utilizando o Spring Security OAuth2 Resource Server.
- **Segurança na Entrada:** Aplicar validações rígidas de Beans (`@Valid`, `@NotNull`) nos adaptadores de entrada para mitigar falhas do OWASP Top 10.
- **Tratamento de Erros:** Qualquer falha de segurança (401 Unauthorized ou 403 Forbidden) ou de negócio deve ser capturada por um `ControllerAdvice` e mapeada estritamente para o padrão **RFC 7807 (Problem Details)**.

## 🔄 3. Concorrência e Idempotência
- **Controle de Concorrência:** Implementar bloqueio otimista utilizando `@Version` na entidade JPA de persistência do pedido.
- **Idempotência:** Filtros de infraestrutura devem capturar o cabeçalho `Idempotency-Key` em requisições mutáveis (`POST`, `DELETE`) para assegurar que transações idênticas não gerem duplicidade no banco ou nos webhooks de pagamento.

## 📊 4. Qualidade e Testes (JaCoCo, PITest & Testcontainers)
- **Testes Unitários:** Desenvolvidos com JUnit 5 e Mockito, focando no isolamento das regras do domínio.
- **Métricas de Qualidade:** Executar o **JaCoCo** exigindo cobertura mínima de 80% do domínio e o **PITest** exigindo score de mutação (MSI) mínimo de 75%.
- **Integração:** Utilizar **Testcontainers** para instanciar containers reais do MySQL e do WireMock em tempo de teste, garantindo o isolamento de stubs do código de produção.

## 🔍 5. Observabilidade, Containerização e CI/CD
- **Métricas e Tracing:** Expor métricas operacionais via Micrometer (`/actuator/prometheus`) e configurar tracing distribuído via OpenTelemetry, injetando o `CorrelationID` em todos os logs estruturados em JSON.
- **Containers:** Fornecer um `Dockerfile` multi-stage e um `docker-compose.yml` completo (Aplicação, MySQL, WireMock, Prometheus, Grafana).
- **Pipeline:** Arquivo do GitHub Actions configurado para compilar, testar (unitários, integrados e mutação) e varrer a imagem final contra vulnerabilidades usando o **Trivy**.

