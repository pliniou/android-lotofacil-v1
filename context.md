# Contexto do Projeto

## Visao Geral

O app gera, valida e analisa jogos da Lotofacil com base no historico oficial de sorteios. O foco e combinar geracao inteligente, auditoria de jogos e analises estatisticas em uma experiencia unificada.

## Arquitetura e Organizacao

- `app/src/main/java/com/cebolao/lotofacil/data`: camada de dados com repositorios, data sources, mapeadores e acesso ao banco.
- `app/src/main/java/com/cebolao/lotofacil/domain`: modelos, contratos de repositorio, use cases e servicos de negocio.
- `app/src/main/java/com/cebolao/lotofacil/ui`: telas, componentes, tema e ViewModels.
- `app/src/main/java/com/cebolao/lotofacil/di`: modulos Hilt para injecao de dependencias.
- `app/src/main/java/com/cebolao/lotofacil/navigation`: destinos e grafo de navegacao.
- `app/src/main/java/com/cebolao/lotofacil/core`: utilitarios, erros, coroutines, extensoes e resultado comum.

## Telas e Navegacao

- `HomeScreen`: resumo do ultimo concurso e estatisticas iniciais.
- `FiltersScreen`: definicao de filtros para geracao de jogos.
- `GeneratedGamesScreen`: exibicao e gerenciamento dos jogos gerados.
- `CheckerScreen`: validacao de jogos contra o historico.
- `StatisticsScreen` (Insights): analise estatistica consolidada.
- `UserStatsScreen`: estatisticas dos jogos do usuario.
- `AboutScreen`: informacoes gerais e atalhos.

## Estados de UI e Fluxo de Inicializacao

- `MainActivity` controla loading e erro global enquanto a base historica inicializa e o sync e disparado em background.
- ViewModels usam `StateViewModel` e `AppResult` para padronizar estados de loading, erro e sucesso.
- O estado de sincronizacao do historico fica em `HistoryRepository.syncStatus` para feedback na UI.

## Principais Fluxos de Negocio

1. Inicializacao: `MainViewModel` dispara `HistoryRepository.syncHistory()` e libera a UI apos a duracao minima de splash.
2. Home: `GetHomeScreenDataUseCase` consome o historico, calcula o ultimo concurso e gera estatisticas iniciais via `StatisticsEngine`.
3. Geracao: `GenerateGamesUseCase` usa `GameGenerator` para criar jogos conforme filtros definidos na UI.
4. Auditoria: `CheckGameUseCase` compara jogos com o historico e `SaveCheckUseCase` persiste o resultado.
5. Estatisticas do usuario: `GetUserGameStatisticsUseCase` combina jogos salvos e historico com `UserStatisticsService`.

## Dados, Persistencia e Cache

- Room armazena sorteios, jogos gerados, checks e cache de estatisticas em `data/datasource/database`.
- Mapeamentos entity-model ficam centralizados em `core/extensions/EntityMappingExtensions.kt`.
- Preferencias do usuario (ex. jogos fixados) usam DataStore em `UserPreferencesRepositoryImpl`.

## Rede e Sincronizacao

- Retrofit com Kotlinx Serialization e OkHttp, com cache e rate limiting.
- Endpoints: Heroku API e Caixa API (configurados em `NetworkModule`).
- `HistoryRepositoryImpl` faz sync incremental por intervalo e controla concorrencia com mutex.

## Design System e Componentes

- Tokens e tema no pacote `ui/theme`.
- Componentes reutilizaveis em `ui/components` para cards, estados de erro e loading.
