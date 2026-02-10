# Observabilidade - Esquema de Eventos

## Princípios
- Sem PII
- Baixa cardinalidade
- Nomes estáveis e versionáveis

## Eventos (exemplos)
1) `feature_opened`
- props: feature_name, source

2) `request_completed`
- props: endpoint_group, status, latency_bucket

3) `sync_result`
- props: type, success, error_category

## Logs
- nível: debug/info/warn/error
- tag: feature
- correlationId: requestId/sessionId (quando aplicável)
