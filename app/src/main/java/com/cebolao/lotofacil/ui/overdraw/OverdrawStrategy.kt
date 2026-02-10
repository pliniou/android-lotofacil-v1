package com.cebolao.lotofacil.ui.overdraw

/**
 * Estratégia de redução de Overdraw no Cebolão Lotofácil.
 *
 * Overdraw ocorre quando um pixel é desenhado mais de uma vez na mesma frame.
 * Isso causa desperdício de GPU e redução de performance.
 *
 * Problemas identificados:
 * 1. **Backgrounds duplicados em Card + Box:**
 *    - Card tem cor padrão (surface)
 *    - Box interno adiciona gradiente/background desnecessário
 *    - Solução: Usar Card com color diretamente ou remover Box background
 *
 * 2. **Transparência em backgrounds:**
 *    - copy(alpha = 0.3f), copy(alpha = 0.2f) causam blending
 *    - Solução: Usar cores sólidas do esquema quando possível
 *
 * 3. **Hierarquia profunda:**
 *    - Scaffold > LazyColumn > Box > Card > Box > Column (6+ níveis)
 *    - Solução: Remover Boxes desnecessários
 *
 * REGRAS DE APLICAÇÃO:
 * ✅ Remover color: Color.Transparent em Card se há background no conteúdo
 * ✅ Remover Box.background() se o Card já fornece background
 * ✅ Mover gradientes para Card se possível, não para Box interno
 * ✅ Usar cores sólidas ao invés de alpha copies quando possível
 * ✅ Remover Boxes que servem apenas para layout (usar Column/Row diretamente)
 * ✅ Validar que cada composable desenha no máximo uma vez por frame
 */
