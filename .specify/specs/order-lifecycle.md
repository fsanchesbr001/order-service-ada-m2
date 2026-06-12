# Spec: Ciclo de Vida do Pedido e Itens

## Segurança e Acesso
- **Endpoints de Mutação (`POST`, `DELETE`):** Exigem escopo `orders:write`.
- **Endpoints de Consulta (`GET`):** Exigem escopo `orders:read`.

## Design SOLID & Regras de Negócio
1. **Agregado de Domínio (SRP):** A classe `Order` centraliza a consistência dos seus itens. O caso de uso apenas busca o pedido, chama o método de negócio da entidade (ex: `order.addItem(...)`) e salva o resultado através da porta `OrderRepositoryPort`.
2. **Confirmação e Cálculo de Preço (OCP):** A transição para `CONFIRMADO` exige ao menos 1 item. A busca de preços atualizados é feita via `CatalogClientPort` (**ISP**). A estratégia de cálculo de preço total pode ser injetada de forma polimórfica caso regras de desconto por volume ou cupom surjam futuramente.
3. **Imutabilidade:** Qualquer tentativa de alteração de itens em pedidos com estado `CONFIRMADO` ou `CANCELADO` resulta em falha de validação rica no domínio.

