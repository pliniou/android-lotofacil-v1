# UiState/UiEvent Contract

## UiState (model)
- Loading:
- Empty:
- Error:
- Content:

## UiEvent (inputs)
- OnClickX
- OnRetry
- OnRefresh

## UiEffect (opcional)
- ShowSnackbar(message)
- Navigate(route)

## Erros (AppError)
- Network
- Unauthorized
- NotFound
- Validation
- Unknown

## Mapeamento para UI
- AppError.Network -> mensagem X + ação Retry
- AppError.Unauthorized -> navegar para login
