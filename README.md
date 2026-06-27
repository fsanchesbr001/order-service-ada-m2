**Projeto Order Service ADA - Módulo 2 - Projeto Final**

**Idealizador:** Fabrício A. S. Sanches

**Objetivo:**
O objetivo deste projeto é entregar uma funcionalidade como meio de avaliação de desempenho deste aluno, durante o módulo 2 do curso de Java da ADA.

## 🚀 Guia de Instalação e Teste via Swagger (Docker + WireMock)

### Pré-requisitos
- Docker e Docker Compose instalados.
- PowerShell (Windows) para gerar JWT com o script do projeto.

### Configuração obrigatória para fluxo completo de pagamento
No serviço `order-service` do `docker-compose.yml`, garanta que estas variáveis estejam configuradas:

```yaml
PAYMENT_GATEWAY_URL: http://wiremock:8080
NOTIFICATION_SERVICE_URL: http://wiremock:8080
```

### Subida do ambiente
```powershell
docker compose up -d --build
```

Se quiser confirmar a porta publicada da API:
```powershell
docker compose ps
```

> Observação: por padrão a API sobe em `http://localhost:8080`, mas pode variar conforme seu `.env`/override.

### Autenticação JWT no Swagger
1. Gere um token:
   ```powershell
   .\scripts\Generate-JWT.ps1
   ```
2. Abra o Swagger: `http://localhost:<porta-da-api>/swagger-ui/index.html`
3. Clique em **Authorize** e cole o token **sem** o prefixo `Bearer `.

### Fluxo de teste completo (8 passos)
1. **Criar pedido**  
   `POST /api/v1/orders`
   ```json
   {
     "customerId": "customer-active-001"
   }
   ```
   Guarde o `orderId`.

2. **Adicionar item ao pedido**  
   `POST /api/v1/orders/{orderId}/items`
   ```json
   {
     "productId": "product-001",
     "productName": "Produto teste",
     "quantity": 2
   }
   ```

3. **(Opcional) Consultar pedido após item**  
   `GET /api/v1/orders/{orderId}`

4. **Confirmar pedido**  
   `POST /api/v1/orders/{orderId}/confirm`  
   Resultado esperado: pedido com status `CONFIRMADO`.

5. **Iniciar pagamento**  
   `POST /api/v1/payments`
   ```json
   {
     "orderId": "<orderId>",
     "paymentMethod": "CREDIT_CARD"
   }
   ```
   Guarde o `paymentId` retornado.

6. **Consultar status inicial do pagamento**  
   `GET /api/v1/payments/{paymentId}`  
   Resultado esperado: `PROCESSING`.

7. **Simular callback do gateway**  
   `POST /api/v1/payments/{paymentId}/callback`
   - Aprovado:
     ```json
     { "status": "APPROVED" }
     ```
   - Recusado:
     ```json
     { "status": "REJECTED" }
     ```

8. **Consultar status final do pagamento**  
   `GET /api/v1/payments/{paymentId}`  
   Resultado esperado: `APPROVED` ou `REJECTED`.

### IDs de teste já mapeados no WireMock
- Cliente válido: `customer-active-001`
- Cliente bloqueado: `customer-blocked-001`
- Cliente inexistente: `customer-not-found-001`
- Produto válido: `product-001`
- Produto sem estoque: `product-out-of-stock-001`
- Produto inexistente: `product-not-found-001`

### Erros comuns
- **401 Unauthorized**: token ausente/inválido/expirado ou `iss` incorreto.
- **403 Forbidden**: token sem escopo necessário.
- **422 Gateway temporariamente indisponível**: faltam `PAYMENT_GATEWAY_URL` e/ou `NOTIFICATION_SERVICE_URL` apontando para o WireMock.

**Histórico de Versões:**

- **Versão 1.0.0 (12-06-2026):** Fase 01 Implementada.
- **Versão 1.0.1 (13-06-2026):** Fase 02 Implementada.
- **Versão 1.0.2 (14-06-2026):** Fase 03 Implementada.
- **Versão 1.0.5 (15-06-2026):** Fase 04 Implementada.
- **Versão 1.0.6 (16-06-2026):** Fase 05 Implementada e testes de requisitos. 
- **Versão 1.0.7 (17-06-2026):** Correção de bugs e fluxos.
- **Versão 1.0.8 (17-06-2026):** Teste de integração.
- **Versão 1.0.9 (17-06-2026):** Teste de integração e cobertura de testes.
- **Versão 1.0.10 (17-06-2026):** Teste de swagger e JWT com Wiremock.
- **Versão 1.0.11 (18-06-2026):** Correção de erro 401 JWT e adição de utilitários de geração de tokens.
- **Versão 1.0.12 (27-06-2026):** Teste de integração com JWT e Wiremock e documentação.
