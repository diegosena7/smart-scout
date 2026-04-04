# Regras de Negocio

## Objetivo

Apoiar a comissao tecnica na avaliacao do elenco a partir de tres dimensoes:

- desempenho
- disciplina
- titularidade

## Principios da modelagem atual

### Desempenho deve respeitar a funcao em campo

O sistema nao avalia todas as posicoes com a mesma formula.

Exemplos:

- atacantes priorizam producao ofensiva e finalizacao
- meias priorizam criacao e construcao
- zagueiros priorizam desarmes, interceptacoes e duelos
- laterais equilibram contribuicoes ofensivas e defensivas
- volantes equilibram recuperacao, cobertura e circulacao
- goleiros priorizam defesas, gols sofridos e solidez defensiva

### Disciplina e transversal

Cartoes e faltas afetam qualquer posicao, por isso o score disciplinar usa a mesma logica para todo o elenco.

### Titularidade e composta

`score_titularidade` combina:

- qualidade tecnica
- comportamento disciplinar

Esse score deve ser interpretado principalmente dentro de disputas por posicao.

## Regras atuais por score

### `score_desempenho`

1. criar metricas derivadas por 90 minutos e taxas
2. aplicar pesos especificos por posicao
3. gerar `desempenho_bruto`
4. normalizar dentro da propria posicao

### `score_disciplina`

1. combinar amarelos, vermelhos e faltas com pesos fixos
2. normalizar o valor bruto
3. inverter o score para que mais disciplina signifique nota maior

### `score_titularidade`

1. combinar desempenho e disciplina com pesos fixos
2. manter `score_desempenho` como componente dominante da recomendacao final

## O que e heuristica hoje

Os pesos atuais nao sao calibrados com historico real do clube. Eles representam uma versao inicial de negocio para prototipagem.

Portanto:

- servem para exploracao
- nao devem ser tratados como modelo definitivo
- devem ser revisados quando houver dados reais ou feedback da comissao

## O que um desenvolvedor pode alterar sem quebrar a arquitetura

Toda a logica de analytics esta concentrada em dois arquivos Java:

- **Pesos de score por posicao** → `backend/src/main/java/com/smartscout/api/service/AnaliseElencoService.java`
  - metodo `computeDesempenhoBruto`: pesos do desempenho por posicao
  - metodo `computeDisciplina`: penalizacao de cartoes e faltas
  - metodo `computeTitularidade`: proporcao desempenho/disciplina
  - metodo `computeMomento`: proporcao titularidade/uso recente

- **Constantes de peso** → `backend/src/main/java/com/smartscout/api/config/ContextoConst.java`
  - `PESO_DISCIPLINA_AMARELO`, `PESO_DISCIPLINA_VERMELHO`, `PESO_DISCIPLINA_FALTA`
  - `PESO_TITULARIDADE_DESEMPENHO`, `PESO_TITULARIDADE_DISCIPLINA`
  - `PESO_MOMENTO_TITULARIDADE`, `PESO_MOMENTO_USO_RECENTE`
  - `DELTA_GANHOU_ESPACO`, `DELTA_PERDEU_ESPACO`

- **Campos exibidos por posicao no frontend** → `frontend/src/constants.js`
  - `METRICAS_POR_POSICAO_UI`: controla quais campos aparecem no formulario por posicao
  - `QUICK_EVENTS_POR_POSICAO`: controla quais botoes de evento rapido aparecem no wizard

## Cuidados ao evoluir

- manter nomes de campos consistentes entre o DTO (`AtuacaoRequest.java`), a entity (`Atuacao.java`) e o frontend (`constants.js`)
- documentar qualquer metrica nova no README
- revisar se a nova metrica faz sentido para a posicao antes de adicionar peso
- evitar comparar jogadores de posicoes diferentes pelo mesmo score de desempenho
- os pesos atuais sao heuristicas iniciais — calibrar com dados reais apos uso em producao
