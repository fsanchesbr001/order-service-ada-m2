# Spec: Validação de Clientes

## Segurança e Acesso
- **Endpoint:** `POST /api/v1/orders`
- **Autorização:** Exige escopo `orders:write`.

## Design SOLID & Regras de Negócio
1. O `OrderController` (Inbound Adapter) recebe a requisição e delega para a porta de entrada `CreateOrderUseCasePort` (**SRP**).
2. O caso de uso interage com o cliente externo exclusivamente através da interface granular `CustomerClientPort` (**ISP** e **DIP**).
3. O adaptador de saída HTTP implementa esta porta consumindo o WireMock. Se precisarmos trocar o WireMock por uma fila gRPC ou REST real no futuro, o caso de uso permanecerá intocado (**OCP**).
4. **Fluxo:** Se o cliente estiver ativo (200), o pedido é gerado. Se estiver bloqueado (422) ou inexistente (404), dispara-se uma exceção de domínio capturada pelo tratador global da aplicação.

