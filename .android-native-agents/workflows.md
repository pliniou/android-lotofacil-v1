# Workflows (playbooks)

> Se você não souber por onde começar, use `master` para roteamento e prompts.

## wf.feature — Nova feature end-to-end
1) `arch` — blueprint (camadas, contratos, estados, DI, erro)  
2) `design` — tokens/estados visuais/a11y rules  
3) `data` — repos/datasources/cache/migrations (se houver)  
4) `android` — permissões/background/deeplinks/resources  
5) `ui` — telas + componentes + previews + a11y  
6) `test` — testes por camada + cenários críticos  
7) `observe` — eventos/logs/erros (definição + instrumentação mínima)  
8) `security` e `perf` — revisão rápida (risco/medição), quando aplicável  

**DoD mínimo (genérico)**
- Compila e roda em pelo menos 1 device/emulador alvo
- Lint/format OK (ou justificativa)
- Testes críticos OK (unit + 1 smoke instrumented se aplicável)
- Estados: loading/empty/error
- A11y básica (semântica/touch targets/contraste via tokens)
- Logs/erros padronizados (sem PII)

## wf.bug — Bugfix com rastreabilidade
1) `observe` — sinal (crash/log/analytics) + hipótese  
2) `arch` — invariantes e contrato quebrado (onde corrigir)  
3) `data`/`ui`/`android` — correção na camada certa (uma só, quando possível)  
4) `test` — teste de regressão  
5) `perf`/`security` — checagem rápida de impacto  

## wf.schema — Mudança de schema / migração
1) `arch` — impacto em modelos e contratos  
2) `data` — migration segura + fallback + compat  
3) `test` — teste de migração + smoke test  
4) `release` — checar shrinker/serialização e rollout  

## wf.release — Preparar release
1) `build` — build reproducível + tasks CI + signing  
2) `test` — suite mínima + sanity instrumented  
3) `perf` — checagem de startup/jank (baseline)  
4) `security` — revisão final (permissões/PII/logs)  
5) `release` — checklist Play + version bump + changelog  

## wf.perf — Performance orientada a métricas
1) `perf` — plano de medição + baseline + hipótese  
2) `observe` — instrumentar métrica/evento se faltar  
3) `ui`/`data`/`android` — otimizações priorizadas  
4) `test` — garantir que comportamento não mudou  

## wf.security — Segurança/privacidade
1) `security` — threat model rápido + permissões mínimas + storage/logs  
2) `android` — validar runtime + exported/intent filters  
3) `observe` — garantir que não há PII em logs/analytics  
4) `test` — casos críticos (sem permissão, sessão expirada, etc.)
