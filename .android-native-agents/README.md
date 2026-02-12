# Android Native Agent Pack (Genérico)

Pacote reutilizável de **agents, skills e workflows** para projetos **Android nativos** (Kotlin + Jetpack),
com responsabilidades bem definidas, hand-offs previsíveis e foco em qualidade (testes, performance,
segurança e observabilidade).

## Objetivos
- Separar responsabilidades por camada (arquitetura, dados, UI, plataforma).
- Facilitar trabalho incremental por feature (contratos → implementação → testes → release).
- Aumentar reuso: o pacote funciona para qualquer app Android moderno (Compose ou View; porém o foco é Compose).

## Estrutura do pacote
- `skills.md` — comandos curtos para uso no chat do IDE.
- `workflows.md` — playbooks (end-to-end, bugfix, schema, release, perf, security).
- `agents/` — um arquivo por agente com: missão, inputs/outputs, limites, checklists e prompts.
- `templates/` — modelos copiáveis (blueprint, ADR, contracts, test matrix, observability, release).

## Como usar no chat do IDE
Use o prefixo da skill e descreva a tarefa. Exemplos:
- `arch`: desenhe contratos e o blueprint da feature "login"
- `data`: implementar repository com cache e retry para "feed"
- `ui`: criar Screen + componentes Compose para "perfil"
- `test`: montar test matrix e escrever testes críticos para "checkout"
- `build`: propor modularização e convenções Gradle para app grande

## Convenções recomendadas (genéricas)
- Camadas: `domain` (regras) → `data` (IO) → `presentation` (UI state) → `platform` (Android APIs).
- UI consome `UiState` e emite `UiEvent` (unidirecional).
- `domain` expõe interfaces; `data` implementa.
- Erros tratados por um `AppError` padronizado e mapeado para UI.

> Dica: Comece qualquer feature por `templates/feature_blueprint.md` e siga `workflows.md`.

## Agente Mestre (Router)
Quando você não souber qual agente usar, invoque:
- `master`: ele roteia para os especialistas e devolve prompts prontos para copiar/colar.

Exemplo:
`master: quero implementar feature "busca" com paginação e offline-first; entregar plano + prompts`
