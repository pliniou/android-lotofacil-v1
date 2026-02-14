# Guia de Voz e Tom

## Personalidade
- Amigável e confiável.
- Educativo sem ser condescendente.
- Otimista, mas realista sobre probabilidades.

## Principios de Escrita
- Fale com clareza e objetividade.
- Prefira frases curtas e diretas.
- Evite jargão técnico sem contexto.
- Em erros, use linguagem empática e orientada à ação.

## Padrões Definidos
- Saudação: `Olá, [nome]`.
- CTA principal: `Gerar jogo`.
- Terminologia oficial no app: `Concurso` (evitar alternar com `Sorteio` em labels principais).

## Mensagens de Erro
- Modelo: `Ops, [o que aconteceu]. [o que fazer agora].`
- Exemplo: `Ops, algo deu errado ao gerar seu jogo. Tente novamente ou ajuste os filtros.`

## Termos Técnicos
- Sempre oferecer explicação no primeiro contato via info/tooltip.
- Exemplos obrigatorios:
  - Fibonacci: sequência `1, 2, 3, 5, 8, 13, 21`.
  - Moldura: dezenas da borda do volante.
  - Miolo/Retrato: dezenas do centro do volante.

## Organização de Microcopy
- Base de textos em `app/src/main/res/values/strings.xml`.
- Grupo adicional por categoria em:
  - `app/src/main/res/values/strings_microcopy.xml`
  - `app/src/main/res/values-pt-rBR/strings_microcopy.xml`
  - `app/src/main/res/values-en/strings_microcopy.xml`
- Mapeamento semântico central:
  - `app/src/main/java/com/cebolao/lotofacil/ui/text/AppStrings.kt`
