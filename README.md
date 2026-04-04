# SmartScout AI

Analytics de desempenho para futebol de várzea. A comissão técnica cadastra jogadores, registra partidas e lança atuações jogo a jogo. O sistema calcula automaticamente scores de desempenho, disciplina, titularidade e momento para cada atleta.

## Stack

| Camada    | Tecnologia                        |
|-----------|-----------------------------------|
| Backend   | Java 17 + Spring Boot 3.3.5       |
| Banco     | PostgreSQL 16                     |
| Frontend  | React 18 + Vite                   |
| Infra     | Docker + Docker Compose           |

## Como rodar

### Pré-requisitos

- Docker Desktop instalado e rodando

### Subir tudo com Docker

```bash
docker-compose up --build
```

Na primeira execução o Maven baixa as dependências e compila o backend — leva alguns minutos. Nas seguintes é instantâneo.

| Serviço  | URL                          |
|----------|------------------------------|
| API      | http://localhost:8000        |
| Frontend | http://localhost:5173        |
| Banco    | localhost:5432               |

### Rodar o frontend separado (desenvolvimento)

```bash
cd frontend
npm install
npm run dev
```

O frontend lê a variável `VITE_API_BASE_URL` ou usa `http://127.0.0.1:8000` como padrão.

## Fluxo de uso

1. **Elenco** — cadastre cada jogador com nome, posição e função tática
2. **Partidas** — registre cada jogo com data, adversário e resultado
3. **Atuações** — use o wizard de 3 steps para lançar os dados de cada jogador por partida:
   - Step 1: selecione a partida
   - Step 2: selecione o jogador
   - Step 3: toque nos eventos (gol, assistência, etc.) e salve
4. Consulte **Dashboard**, **Análise do elenco**, **Comparar jogadores** e **Time ideal**

## Estrutura do projeto

```
smartscout-ai/
  backend/                        # Java / Spring Boot
    src/main/java/com/smartscout/api/
      controller/                 # Endpoints REST
      service/                    # Lógica de negócio e analytics
      entity/                     # JPA entities (Jogador, Partida, Atuacao)
      repository/                 # Spring Data repositories
      dto/                        # Request/response shapes
      config/                     # CORS, constantes
    src/main/resources/
      application.properties      # Datasource, porta (8000), JPA
    Dockerfile                    # Multi-stage: Maven build + JRE runtime
  frontend/                       # React / Vite
    src/
      App.jsx                     # Estado global + roteamento
      api.js                      # Cliente HTTP
      constants.js                # Constantes e configurações de campos
      utils.js                    # Funções utilitárias puras
      components/                 # Componentes reutilizáveis
        DataTable.jsx
        PitchView.jsx
        RadarComparacao.jsx
        ComparacaoTable.jsx
        EvolucaoJogador.jsx
        PlayerRows.jsx
      pages/                      # Telas da aplicação
        Dashboard.jsx
        EntradaDados.jsx          # Inclui o wizard de atuações
        AnaliseElenco.jsx
        ComparacaoJogadores.jsx
        TimeIdeal.jsx
      styles.css                  # Design system dark-green completo
  docs/
    regras_de_negocio.md          # Lógica dos scores e princípios da modelagem
  docker-compose.yml              # PostgreSQL + backend
```

## API REST

| Método | Endpoint                          | Descrição                          |
|--------|-----------------------------------|------------------------------------|
| GET    | /health                           | Health check                       |
| GET    | /meta/contexto                    | Valores válidos de posição, formação etc. |
| GET    | /jogadores                        | Lista todos os jogadores           |
| POST   | /jogadores                        | Cria jogador                       |
| GET    | /partidas                         | Lista todas as partidas            |
| POST   | /partidas                         | Cria partida                       |
| GET    | /atuacoes                         | Lista todas as atuações            |
| POST   | /atuacoes                         | Cria atuação                       |
| PUT    | /atuacoes/{id}                    | Atualiza atuação                   |
| DELETE | /atuacoes/{id}                    | Remove atuação                     |
| GET    | /analise/elenco?posicao=          | Análise do elenco (filtrável)      |
| GET    | /analise/jogador/{nome}           | Análise individual                 |
| GET    | /analise/comparar?jogador_a=&jogador_b= | Comparação 1x1              |
| GET    | /analise/time-ideal?formacao=     | XI ideal por formação              |
| GET    | /dashboard/overview               | Dados do dashboard                 |

## Scores calculados

O engine de analytics roda em `AnaliseElencoService.java` a cada requisição de análise. Nenhum dado é persistido — tudo é recalculado na hora.

| Score               | O que mede                                          |
|---------------------|-----------------------------------------------------|
| score_desempenho    | Rendimento técnico por posição (normalizado 0-100)  |
| score_disciplina    | Custo disciplinar invertido (mais disciplina = maior score) |
| score_titularidade  | 79% desempenho + 21% disciplina                     |
| score_uso_recente   | Minutos nos últimos 5 jogos vs. média histórica     |
| score_momento       | 70% titularidade + 30% uso recente                  |
| tendencia_recente   | ganhou espaço / estável / perdeu espaço             |

Consulte `docs/regras_de_negocio.md` para as fórmulas detalhadas por posição.

## Variáveis de ambiente (backend)

| Variável                    | Padrão (application.properties)              |
|-----------------------------|----------------------------------------------|
| SPRING_DATASOURCE_URL       | jdbc:postgresql://localhost:5432/smartscout  |
| SPRING_DATASOURCE_USERNAME  | smartscout                                   |
| SPRING_DATASOURCE_PASSWORD  | smartscout                                   |
| server.port                 | 8000                                         |

No Docker Compose, `SPRING_DATASOURCE_URL` aponta para o serviço `db` via rede interna.

## Posições e formações suportadas

**Posições:** Goleiro, Zagueiro, Lateral, Volante, Meia, Atacante

**Formações:** 4-3-3, 4-4-2, 4-2-3-1
