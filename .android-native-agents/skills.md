# Skills (comandos curtos)

Use estes nomes como "prefixo" no chat do IDE.

| Skill | Responsabilidade |
|---|---|
| `master` | Roteador: escolhe o(s) agente(s) certo(s), ordem e prompts prontos |
| `arch` | Arquitetura, boundaries, contratos, DI, UiState/UiEvent, erros |
| `data` | Rede/persistência/cache/sync/migrations (Room/Retrofit/DataStore) |
| `ui` | Jetpack Compose: telas, componentes, previews, a11y na UI |
| `android` | Android framework: lifecycle, permissions, intents, WorkManager, manifest |
| `kotlin` | Idioms/refactors/coroutines, melhorias de legibilidade e segurança |
| `design` | Design system: tokens, Material 3, WCAG, responsividade |
| `test` | Estratégia e implementação de testes (unit/instrumented/compose) |
| `build` | Gradle/AGP/deps/modularização/CI/lint/detekt/ktlint |
| `release` | Versionamento, signing, R8, checklist Play, changelog |
| `security` | Segurança & privacidade: storage, segredos, permissões, PII |
| `perf` | Performance: startup, jank, memória, rede, bateria |
| `observe` | Observabilidade: logs, crash, métricas, analytics, tracing |

## Padrão de prompt
`<skill>: <tarefa> | contexto: <restrições> | entregar: <artefatos>`

Exemplo:
`arch: blueprint de feature "busca" | contexto: offline-first, paginação | entregar: contratos + UiState + DI`
