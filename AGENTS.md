# AGENTS.md

## Proposito do projeto

Sistema de analytics esportivo para apoiar a comissao tecnica com base em:

- desempenho tecnico por posicao (scores normalizados dentro da posicao)
- disciplina (cartoes e faltas)
- recomendacao de titularidade e momento recente

## Stack

- **Backend:** Java 17 + Spring Boot 3 + Spring Data JPA
- **Banco de dados:** PostgreSQL 16 (via Docker)
- **Frontend:** React 18 + Vite + Recharts
- **Build backend:** Maven
- **Build frontend:** npm

## Execucao

### Banco de dados

```bash
docker-compose up -d
```

### Backend (Spring Boot)

```bash
cd backend
mvn spring-boot:run
```

API disponivel em `http://localhost:8000`. Documentacao dos endpoints em `http://localhost:8000/swagger-ui` (se habilitado).

### Frontend (React)

```bash
cd frontend
npm install
npm run dev
```

Frontend disponivel em `http://localhost:5173`.

## Estrutura de pastas

```
backend/
  src/main/java/com/smartscout/api/
    config/          CorsConfig.java, ContextoConst.java
    entity/          Jogador.java, Partida.java, Atuacao.java
    repository/      JogadorRepository.java, PartidaRepository.java, AtuacaoRepository.java
    dto/             JogadorRequest.java, PartidaRequest.java, AtuacaoRequest.java
    service/         AnaliseElencoService.java, RelatorioService.java, JogadorService.java,
                     PartidaService.java, AtuacaoService.java, PlayerCalc.java
    controller/      JogadorController.java, PartidaController.java, AtuacaoController.java,
                     AnaliseController.java, DashboardController.java, MetaController.java
  src/main/resources/
    application.properties
frontend/
  src/
    App.jsx          componentes e logica de UI
    api.js           cliente HTTP para o backend
    styles.css       design system (tema escuro, verde)
docker-compose.yml   PostgreSQL
```

## Fontes de verdade

- Arquitetura: `docs/arquitetura.md`
- Regras de negocio e formulas: `docs/regras_de_negocio.md`
- Pesos e constantes: `backend/src/main/java/com/smartscout/api/config/ContextoConst.java`
- Formulas de score por posicao: `AnaliseElencoService.computeDesempenhoBruto()`

Antes de alterar formulas ou pesos, revisar `docs/regras_de_negocio.md`.

## Onde alterar cada tipo de regra

- Pesos de score e formacoes: `ContextoConst.java`
- Formulas de desempenho por posicao: `AnaliseElencoService.computeDesempenhoBruto()`
- Normalizacao e scores: `AnaliseElencoService` (normalizeByPosition, computeDisciplina, etc.)
- Pipeline orquestrado: `AnaliseElencoService.construirAnalise()`
- Consultas e comparacoes: `RelatorioService.java`
- Endpoints REST: controllers em `controller/`
- Entidades e schema do banco: `entity/`

## Invariantes criticas

- `score_desempenho` e sempre normalizado dentro da `posicao`, nunca no elenco inteiro.
- `score_disciplina` e invertido: custo disciplinar maior resulta em score menor.
- Metricas ofensivas e defensivas devem usar valor por 90 minutos, nao acumulado bruto.
- Se todos os jogadores de um grupo tiverem o mesmo valor, normalizacao retorna `50`.
- `score_titularidade = score_desempenho * 0.79 + score_disciplina * 0.21`
- `score_momento = score_titularidade * 0.70 + score_uso_recente * 0.30`
- Tendencia: delta >= 8 -> "ganhou espaco", delta <= -8 -> "perdeu espaco", senao "estavel".
- `minutos_ultimos_5_jogos` e calculado dinamicamente dos ultimos 5 jogos do atleta por data de partida.

## Regras de mudanca

- Nao adicionar nova posicao sem definir pesos em `AnaliseElencoService.computeDesempenhoBruto()`.
- Nao alterar nomes de campos da entidade sem atualizar repositorios, services e documentacao.
- Nao duplicar regra de negocio: constantes em `ContextoConst`, formulas em `AnaliseElencoService`.
- Mudancas nos pesos devem ser refletidas em `ContextoConst.java` e `docs/regras_de_negocio.md`.

## Validacao minima apos alteracoes

- Subir o banco: `docker-compose up -d`
- Buildar e subir o backend: `cd backend && mvn spring-boot:run`
- Verificar `/health` retorna `{"status":"ok"}`
- Verificar `/analise/elenco` retorna jogadores com scores corretos
- Verificar `/analise/time-ideal` monta escalacao para formacao 4-3-3
- Verificar frontend em `http://localhost:5173` abre sem erros
