# Arquitetura

## Visao geral

```
Frontend (React + Vite)
    -> HTTP REST
Backend (Spring Boot 3 + Java 17)
    -> Spring Data JPA
PostgreSQL 16 (Docker)
```

## Stack

| Camada | Tecnologia |
|---|---|
| Frontend | React 18, Vite, Recharts |
| Backend | Java 17, Spring Boot 3, Maven |
| ORM | Spring Data JPA / Hibernate |
| Banco | PostgreSQL 16 |
| Infraestrutura | Docker Compose |

## Estrutura do backend

```
config/
  CorsConfig.java          CORS para localhost:5173 e localhost:3000
  ContextoConst.java       Pesos, formacoes e listas de valores validos

entity/
  Jogador.java             Cadastro base do elenco
  Partida.java             Registro de cada jogo
  Atuacao.java             Estatisticas por jogador por partida

repository/
  JogadorRepository.java
  PartidaRepository.java
  AtuacaoRepository.java

dto/
  JogadorRequest.java      Payload de entrada para cadastro de jogador
  PartidaRequest.java      Payload de entrada para cadastro de partida
  AtuacaoRequest.java      Payload de entrada para lancamento de atuacao

service/
  JogadorService.java      CRUD de jogadores
  PartidaService.java      CRUD de partidas (com geracao de ID)
  AtuacaoService.java      CRUD de atuacoes
  PlayerCalc.java          Objeto mutavel interno do pipeline analitico
  AnaliseElencoService.java Pipeline completo: agrega -> per90 -> normaliza -> scores
  RelatorioService.java    Consultas: contexto, dashboard, busca, comparacao, time ideal

controller/
  MetaController.java      GET /health, GET /meta/contexto
  DashboardController.java GET /dashboard/overview
  JogadorController.java   GET /jogadores, POST /jogadores
  PartidaController.java   GET /partidas, POST /partidas
  AtuacaoController.java   GET/POST /atuacoes, PUT/DELETE /atuacoes/{id}
  AnaliseController.java   GET /analise/elenco, /jogador, /comparar, /time-ideal, /validacao
```

## Pipeline analitico (AnaliseElencoService)

```
Atuacoes (banco)
    -> aggregate por jogador_id (sum stats, count jogos, sum minutos)
    -> calcular minutos_ultimos_5_jogos (ultimos 5 por data de partida)
    -> computeMetricasPor90 (gols_por90, desarmes_por90, etc.)
    -> computeDesempenhoBruto (formula por posicao com pesos)
    -> normalizeByPosition (desempenho_bruto -> score_desempenho, min-max dentro da posicao)
    -> computeDisciplina (custo bruto invertido -> score_disciplina, normalizacao global)
    -> computeTitularidade (79% desempenho + 21% disciplina)
    -> computeUsoRecente (indice_uso_recente = minutos_5 / expectativa)
    -> normalizeByPosition (indice_uso_recente -> score_uso_recente)
    -> computeMomento (70% titularidade + 30% uso_recente, delta, tendencia)
    -> computeRankings (rank por posicao para titularidade e momento)
    -> List<Map> (resposta JSON)
```

## Serializacao JSON

Jackson configurado com `SNAKE_CASE` naming strategy:
- campos Java `camelCase` serializam como `snake_case` automaticamente
- ex: `jogadorId` -> `jogador_id`, `posicaoJogo` -> `posicao_jogo`
- datas `LocalDate` serializam como string `"YYYY-MM-DD"`

## Decisoes de design

### Normalizacao por posicao no desempenho
`score_desempenho` e normalizado dentro da propria posicao porque atacantes e goleiros nao devem ser comparados pela mesma regua.

### Score disciplina com inversao
Custo disciplinar bruto maior -> score final menor. Isso premia comportamento disciplinado naturalmente.

### minutos_ultimos_5_jogos calculado dinamicamente
Em vez de manter uma coluna separada (como o fallback legado fazia), o sistema calcula os minutos dos ultimos 5 jogos do atleta em tempo real a partir das atuacoes, ordenadas pela data da partida.

### IDs como UUID/string
Jogadores e atuacoes usam UUID gerado no backend. Partidas usam ID composto: `{data}-{slug-adversario}-{uuid-curto}` para ser reconhecivel na interface.

## Como evoluir

- Autenticacao: adicionar Spring Security com JWT
- Cache: adicionar `@Cacheable` no `AnaliseElencoService.construirAnalise()` para evitar recalculo em cada request
- Paginacao: adicionar `Pageable` nos endpoints de listagem quando o elenco crescer
- Migracao de schema: substituir `ddl-auto=update` por Flyway ou Liquibase em producao
