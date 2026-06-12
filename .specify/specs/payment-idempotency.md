# Spec: Processamento de Pagamento e Idempotência

## Segurança e Acesso
- **Endpoint de Início (`POST /api/v1/payments`):** Exige escopo `payments:write`.
- **Endpoint de Webhook (`POST /api/v1/payments/{paymentId}/callback`):** Protegido por autenticação restrita de infraestrutura do Gateway.

## Design SOLID & Regras de Negócio
1. **Isolamento de Infraestrutura (DIP/SRP):** O mecanismo de interceptação do cabeçalho `Idempotency-Key` é isolado num componente de infraestrutura (Filtro ou Interceptor do Spring). Ele valida se a chave já foi processada consultando a porta `IdempotencyStoragePort`.
2. O domínio não sabe o que é um cabeçalho HTTP; ele recebe apenas o comando de negócio processado.
3. **Regra de Retentativas (SRP):** A lógica de controle do contador de falhas fica na entidade de domínio `Order`. Ao atingir a 3ª rejeição retornada pelo gateway através da porta de pagamento, o próprio método do domínio transiciona o estado para `CANCELADO`.

