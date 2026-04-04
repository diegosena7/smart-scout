# Sistema de Analise de Jogadores para Comissao Tecnica

Projeto de analytics esportivo para apoiar decisoes da comissao tecnica com base em dados de desempenho, disciplina, saude e carga de jogos.

## Objetivo

Responder perguntas como:

- quem esta jogando melhor
- quem cria mais chances
- quem esta mais indisciplinado
- quem deve ser titular

## Estrutura

- `api/`: backend FastAPI para expor cadastro e analise via HTTP
- `data/raw/`: bases de entrada em CSV
- `docs/`: documentacao de arquitetura e regras de negocio
- `frontend/`: aplicacao React responsiva
- `notebook/`: exploracao e prototipos analiticos
- `src/`: funcoes para carga, consolidacao e scoring

## Execucao web

### Backend FastAPI

Instale as dependencias do projeto:

```bash
pip install -r requirements.txt
```

Suba a API na raiz do projeto:

```bash
uvicorn api.main:app --reload
```

A API fica disponivel em `http://127.0.0.1:8000` e a documentacao interativa em `http://127.0.0.1:8000/docs`.

### Frontend React

Entre na pasta do frontend e instale as dependencias:

```bash
cd frontend
npm install
```

Depois rode a aplicacao:

```bash
npm run dev
```

O frontend usa proxy para a API e abre em `http://127.0.0.1:5173`.

### Fluxo web principal

1. abrir o `Dashboard` para ver titulares recomendados, destaques de momento e ultimos lancamentos
2. entrar em `Entrada de dados`
3. usar a subaba `Elenco` para cadastrar o jogador uma unica vez
4. usar a subaba `Partidas` para registrar cada jogo
5. usar a subaba `Atuacoes` para lancar, editar ou excluir o desempenho por jogador
6. revisar `Analise do elenco`, `Comparar jogadores` e `Time ideal`

### Campos controlados no cadastro

Para manter consistencia analitica, o frontend e a API usam valores controlados para:

- `funcao`: centroavante, falso 9, ponta direita, meia central, volante construtor, ala-direito, zagueiro construtor, goleiro linha e outras opcoes predefinidas
- `pe_dominante`: `Direito`, `Esquerdo` ou `Ambidestro`

## Documentacao tecnica

- `docs/arquitetura.md`: explica a organizacao dos modulos, fluxo de dados e decisoes de design
- `docs/regras_de_negocio.md`: explica a logica dos scores e os principios da modelagem

## Bases atuais

O projeto agora suporta dois modos de entrada:

- `fluxo operacional da comissao`: cadastro de jogadores, partidas e atuacoes por jogo
- `fallback legado`: CSVs acumulados sinteticos, mantidos apenas para testes e transicao

Bases operacionais:

- `jogadores_cadastrados.csv`: cadastro base do elenco
- `partidas.csv`: jogos cadastrados pela comissao
- `atuacoes_partidas.csv`: lancamentos por jogador em cada partida

Fallback legado:

- `desempenho_jogadores.csv`: desempenho tecnico por jogador
- `disciplina_jogadores.csv`: cartoes e faltas
- `lesoes_jogadores.csv`: historico de lesoes
- `carga_jogadores.csv`: minutos, sequencia de jogos e descanso

Os CSVs legados seguem no projeto apenas para validacao da consolidacao e como fallback enquanto nao houver atuacoes lancadas.

## Dicionario de dados

### `jogadores_cadastrados.csv`

- `jogador_id`: identificador tecnico unico do atleta
- `jogador`: nome do atleta
- `posicao`: posicao principal do elenco
- `status`: `Ativo` ou `Inativo`
- `funcao`: papel tatico opcional
- `pe_dominante`: informacao opcional
- `observacoes`: anotacoes livres da comissao

### `partidas.csv`

- `partida_id`: identificador unico da partida
- `data_partida`: data no formato `YYYY-MM-DD`
- `adversario`: adversario enfrentado
- `competicao`: nome da competicao ou torneio
- `mandante`: `Casa` ou `Fora`
- `resultado`: `Vitoria`, `Empate` ou `Derrota`
- `formacao`: formacao usada no jogo
- `observacoes`: anotacoes livres

### `atuacoes_partidas.csv`

- `atuacao_id`: identificador unico da atuacao
- `partida_id`: chave da partida
- `jogador_id`: chave tecnica do atleta no cadastro base
- `jogador`: atleta avaliado
- `posicao_jogo`: posicao usada naquela partida
- `minutos_jogados`: minutos no jogo
- eventos tecnicos: gols, assistencias, finalizacoes, passes, desarmes, interceptacoes, recuperacoes, cruzamentos, defesas, gols sofridos
- eventos disciplinares: amarelos, vermelhos, faltas

### Homonimos no elenco

O cadastro agora usa `jogador_id` como chave tecnica interna. Isso permite ter mais de um atleta com o mesmo nome no elenco.

Na subaba `Entrada de dados > Atuacoes`:

- o seletor de jogador mostra `nome + posicao`
- o valor salvo usa `jogador_id`, nao apenas o nome
- ao selecionar o atleta, o campo `Posicao no jogo` e preenchido automaticamente com a posicao principal cadastrada
- a interface mostra apenas metricas compativeis com a posicao selecionada
- por seguranca, o backend zera metricas que nao pertencem a posicao informada na atuacao
- volante, lateral e zagueiro tambem podem registrar `gols` e `assistencias`, mas com peso menor do que nas posicoes ofensivas

Isso reduz erro operacional no lancamento de atuacao quando houver homonimos.

Nas consultas analiticas:

- comparacao entre jogadores usa `jogador_id` internamente
- consulta individual aceita `jogador_id` para leitura exata de um atleta
- buscas livres por nome continuam disponiveis, mas podem retornar mais de um registro quando houver homonimos

Os CSVs legados abaixo seguem no projeto apenas como fallback de testes.

### `desempenho_jogadores.csv`

- `jogador`: nome do jogador
- `posicao`: funcao principal em campo
- `jogos`: quantidade de jogos disputados
- `gols`: gols marcados
- `assistencias`: assistencias realizadas
- `finalizacoes`: total de finalizacoes
- `finalizacoes_no_alvo`: finalizacoes no alvo
- `passes_decisivos`: passes que geraram finalizacao
- `passes_certos`: passes certos
- `desarmes`: desarmes realizados
- `duelos_ganhos`: duelos vencidos
- `minutos_jogados`: minutos totais jogados
- `interceptacoes`: bolas interceptadas
- `recuperacoes_bola`: recuperacoes de posse
- `cruzamentos_certos`: cruzamentos certos
- `defesas_dificeis`: defesas de alta exigencia tecnica
- `gols_sofridos`: gols sofridos pelo goleiro
- `jogos_sem_sofrer_gols`: partidas sem sofrer gols

### `disciplina_jogadores.csv`

- `jogador`: nome do jogador
- `cartoes_amarelos`: cartoes amarelos recebidos
- `cartoes_vermelhos`: cartoes vermelhos recebidos
- `faltas_cometidas`: faltas cometidas

### `lesoes_jogadores.csv`

- `jogador`: nome do jogador
- `tipo_lesao`: categoria da lesao
- `local_lesao`: regiao corporal afetada
- `dias_afastado`: dias em recuperacao ou afastado
- `reincidencia`: indica se houve reincidencia da lesao (`1` sim, `0` nao)
- `dias_desde_ultima_lesao`: quantos dias se passaram desde a ultima lesao

### `carga_jogadores.csv`

- `jogador`: nome do jogador
- `minutos_ultimos_5_jogos`: minutos acumulados nos ultimos 5 jogos
- `jogos_consecutivos`: quantidade de partidas seguidas sem poupado
- `dias_descanso`: dias de descanso antes do jogo mais recente
- `sessoes_alta_intensidade`: sessoes recentes de treino ou carga alta

## Indicadores iniciais

- `score_desempenho`: combina producao ofensiva, criacao, eficiencia de passe e contribuicao defensiva
- `score_disciplina`: penaliza amarelos, vermelhos e faltas
- `score_titularidade`: score composto para apoiar recomendacao de titulares

## Como os scores sao calculados

Os scores sao normalizados em escala de `0` a `100` com base no elenco atual.

### Normalizacao

Para os scores derivados, o projeto usa uma normalizacao min-max:

```text
score = ((valor - minimo) / (maximo - minimo)) * 100
```

Se todos os jogadores tiverem o mesmo valor em uma metrica, o sistema retorna `50` para todos nessa etapa.

### `score_desempenho`

Mede o rendimento tecnico do jogador de acordo com sua funcao em campo. O score bruto e calculado por posicao e a normalizacao final tambem acontece dentro da propria posicao.

Metricas auxiliares:

- `gols_por90 = (gols * 90) / minutos_jogados`
- `participacoes_gol_por90 = ((gols + assistencias) * 90) / minutos_jogados`
- `taxa_finalizacoes_no_alvo = finalizacoes_no_alvo / finalizacoes`
- `passes_decisivos_por90 = (passes_decisivos * 90) / minutos_jogados`
- `assistencias_por90 = (assistencias * 90) / minutos_jogados`
- `finalizacoes_no_alvo_por90 = (finalizacoes_no_alvo * 90) / minutos_jogados`
- `desarmes_por90 = (desarmes * 90) / minutos_jogados`
- `passes_certos_por90 = (passes_certos * 90) / minutos_jogados`
- `duelos_ganhos_por90 = (duelos_ganhos * 90) / minutos_jogados`
- `interceptacoes_por90 = (interceptacoes * 90) / minutos_jogados`
- `recuperacoes_bola_por90 = (recuperacoes_bola * 90) / minutos_jogados`
- `cruzamentos_certos_por90 = (cruzamentos_certos * 90) / minutos_jogados`
- `defesas_dificeis_por90 = (defesas_dificeis * 90) / minutos_jogados`
- `gols_sofridos_por90 = (gols_sofridos * 90) / minutos_jogados`
- `gols_sofridos_por90_invertido = 1 / (1 + gols_sofridos_por90)`
- `jogos_sem_sofrer_gols_por_jogo = jogos_sem_sofrer_gols / jogos`

Formula por posicao:

```text
Atacante
desempenho_bruto =
    gols_por90 * 0.35 +
    assistencias_por90 * 0.20 +
    finalizacoes_no_alvo_por90 * 0.20 +
    taxa_finalizacoes_no_alvo * 20 * 0.10 +
    passes_decisivos_por90 * 0.15

Meia
desempenho_bruto =
    assistencias_por90 * 0.25 +
    passes_decisivos_por90 * 0.35 +
    passes_certos_por90 * 0.20 +
    gols_por90 * 0.20

Zagueiro
desempenho_bruto =
    desarmes_por90 * 0.25 +
    interceptacoes_por90 * 0.25 +
    duelos_ganhos_por90 * 0.30 +
    passes_certos_por90 * 0.20

Lateral
desempenho_bruto =
    desarmes_por90 * 0.20 +
    interceptacoes_por90 * 0.20 +
    cruzamentos_certos_por90 * 0.25 +
    passes_decisivos_por90 * 0.20 +
    recuperacoes_bola_por90 * 0.15

Volante
desempenho_bruto =
    desarmes_por90 * 0.30 +
    interceptacoes_por90 * 0.25 +
    recuperacoes_bola_por90 * 0.25 +
    passes_certos_por90 * 0.20

Goleiro
desempenho_bruto =
    defesas_dificeis_por90 * 0.45 +
    gols_sofridos_por90_invertido * 0.30 +
    jogos_sem_sofrer_gols_por_jogo * 100 * 0.25
```

Depois disso, `desempenho_bruto` e normalizado dentro de cada `posicao` para gerar `score_desempenho`.

### `score_disciplina`

Mede o custo disciplinar do jogador. Quanto maior o numero de cartoes e faltas, pior o resultado.

Formula bruta:

```text
disciplina_bruta =
    cartoes_amarelos * 1.5 +
    cartoes_vermelhos * 6 +
    faltas_cometidas * 0.4
```

Como valores mais altos representam pior disciplina, o score final e invertido:

```text
score_disciplina = 100 - normalizacao(disciplina_bruta)
```

### `score_titularidade`

Representa uma recomendacao composta para apoiar a escolha de titulares dentro de cada disputa posicional.

Depois aplica a combinacao final:

```text
score_titularidade =
    score_desempenho * 0.79 +
    score_disciplina * 0.21
```

Interpretacao dos pesos:

- `79%` desempenho tecnico
- `21%` disciplina

Os pesos atuais sao heuristicas iniciais e podem ser recalibrados com apoio da comissao tecnica.

Na interface, esse campo aparece com nome mais direto: `Forca na posicao`.

## Indicadores de momento recente

Para apoiar decisoes de curto prazo, o projeto adiciona uma camada complementar de leitura recente.

Importante:

- essa camada nao usa historico jogo a jogo
- ela e uma heuristica de momento com base em uso recente
- portanto, serve como sinal complementar e nao como verdade absoluta

### `indice_uso_recente`

Mede quanto o jogador foi utilizado nos ultimos 5 jogos em relacao ao que seria esperado para ele pelo seu historico medio.

Formula:

```text
expectativa_uso_recente = (minutos_jogados / jogos) * 5

indice_uso_recente = minutos_ultimos_5_jogos / expectativa_uso_recente
```

Interpretacao:

- valor acima de `1` significa que o atleta foi mais utilizado recentemente do que sua media historica
- valor perto de `1` indica manutencao do uso esperado
- valor abaixo de `1` sugere perda de minutos recentes

### `score_uso_recente`

Transforma `indice_uso_recente` em score de `0` a `100`, normalizado dentro da propria `posicao`.

Formula:

```text
score_uso_recente = normalizacao_por_posicao(indice_uso_recente)
```

Interpretacao:

- nota maior significa que, entre os atletas da mesma posicao, o jogador esta entre os mais utilizados recentemente
- esse score nao mede qualidade tecnica diretamente
- ele mede sinal de utilizacao recente

Na interface, esse campo pode ser tratado como indicador de apoio e nao precisa ser protagonista da leitura principal.

### `score_momento`

Combina a forca estrutural do jogador com seu uso recente.

Formula:

```text
score_momento =
    score_titularidade * 0.70 +
    score_uso_recente * 0.30
```

Interpretacao:

- `70%` do peso continua vindo da qualidade consolidada do atleta
- `30%` entra como ajuste de momento recente
- isso evita que poucos minutos recentes derrubem totalmente um jogador forte, mas permite captar mudancas de espaco no curto prazo

Na interface, esse campo aparece com nome mais direto: `Momento atual`.

### `delta_momento`

Mede a diferenca entre o valor recente e a base estrutural do atleta.

Formula:

```text
delta_momento = score_momento - score_titularidade
```

Interpretacao:

- valor positivo indica que o momento recente esta melhorando a leitura do atleta
- valor negativo indica que o momento recente esta puxando a leitura para baixo
- quanto maior o modulo, maior o descolamento entre base e momento

### `tendencia_recente`

Classifica o momento do jogador em tres estados:

```text
se delta_momento >= 8    -> "ganhou espaco"
se delta_momento <= -8   -> "perdeu espaco"
senao                    -> "estavel"
```

Interpretacao:

- `ganhou espaco`: o uso recente fortalece a leitura atual do atleta
- `perdeu espaco`: o uso recente enfraquece a leitura atual do atleta
- `estavel`: nao houve variacao suficiente para gerar alerta

### `rank_titularidade_posicao`

Ordena os jogadores dentro da propria `posicao` usando `score_titularidade` em ordem decrescente.

Interpretacao:

- `1` significa melhor nome atual da posicao no recorte estrutural
- quanto menor o rank, mais forte e a candidatura do atleta como titular da posicao

Na interface, esse campo aparece com nome mais direto: `Ranking na posicao`.

### `rank_momento_posicao`

Ordena os jogadores dentro da propria `posicao` usando `score_momento` em ordem decrescente.

Interpretacao:

- `1` significa melhor nome da posicao considerando o momento recente
- esse rank pode divergir do rank estrutural e sinalizar disputa aberta ou mudanca de hierarquia

## Como as novas consultas usam esses campos

### `consultar_jogador_por_nome(nome)`

Retorna o recorte completo do atleta buscado por nome, com busca parcial e sem diferenciar maiusculas de minusculas.

Exemplo:

```python
consultar_jogador_por_nome("hug")
```

### `comparar_jogadores(jogador_a, jogador_b)`

Compara dois jogadores nos campos principais:

- `score_desempenho`
- `score_disciplina`
- `score_titularidade`
- `score_uso_recente`
- `score_momento`
- `tendencia_recente`
- `rank_titularidade_posicao`
- `rank_momento_posicao`

Leitura sugerida:

- use `score_titularidade` para comparar a forca estrutural
- use `score_momento` para comparar o momento recente
- use os ranks para saber quem lidera a disputa dentro da posicao

### `montar_time_ideal_por_formacao(formacao)`

Seleciona automaticamente o melhor time para uma formacao predefinida.

Regra:

- para cada posicao exigida pela formacao, o sistema escolhe os jogadores com maior `score_titularidade`
- em caso de empate pratico, `score_momento` ajuda a desempatar

Formacoes atuais:

- `4-3-3`
- `4-4-2`
- `4-2-3-1`

### `explicar_recomendacao_jogador(nome)`

Gera um texto curto para apoiar a interpretacao da recomendacao.

A funcao resume:

- posicao do atleta
- `rank_titularidade_posicao`
- `score_titularidade`
- `score_desempenho`
- `score_disciplina`
- `score_momento`
- `tendencia_recente`

Exemplo de leitura:

- se o texto disser que o atleta e `1` no ranking da posicao, ele lidera a disputa estrutural
- se a tendencia vier como `perdeu espaco`, o atleta ainda pode liderar, mas com sinal de perda recente de espaco competitivo

### `analisar_tendencias()`

Retorna uma tabela consolidada para leitura de momento do elenco.

Campos principais:

- `score_titularidade`
- `score_uso_recente`
- `score_momento`
- `delta_momento`
- `tendencia_recente`
- `rank_titularidade_posicao`
- `rank_momento_posicao`

Uso recomendado:

- identificar atletas em alta
- identificar atletas em baixa
- encontrar disputas de posicao mais abertas

## Como usar

Crie o ambiente e instale as dependencias:

```bash
pip install -r requirements.txt
```

Execute o notebook:

```bash
jupyter notebook
```

Execute a interface minima desktop:

```bash
python app.py
```

Ou rode uma consolidacao rapida no terminal:

```bash
python -c "from src.analise_jogadores.analise_elenco import construir_analise_elenco; print(construir_analise_elenco().head())"
```

## Consultas prontas

O modulo `src.analise_jogadores.relatorio` inclui consultas para comparacoes por posicao:

```python
from src.analise_jogadores.relatorio import (
    analisar_tendencias,
    comparar_jogadores,
    comparar_metricas_posicao,
    comparar_por_posicao,
    consultar_jogador_por_nome,
    explicar_recomendacao_jogador,
    montar_time_ideal_por_formacao,
    principais_titulares_por_posicao,
)

comparar_por_posicao("Atacante")
comparar_metricas_posicao("Meia")
principais_titulares_por_posicao()
consultar_jogador_por_nome("Enzo")
comparar_jogadores("Enzo", "Pedro")
montar_time_ideal_por_formacao("4-3-3")
explicar_recomendacao_jogador("Hugo")
analisar_tendencias()
```

## Interface minima

O projeto inclui uma interface desktop em `tkinter` para facilitar a operacao sem depender de notebook.

Telas disponiveis:

- `Cadastrar jogador`
- `Cadastrar partida`
- `Lancar atuacao`
- `Consolidar e validar`
- `Ver analise do elenco`
- `Comparar jogadores`
- `Montar time ideal`

Objetivo da interface:

- registrar a operacao real da comissao
- transformar atuacoes por jogo em base consolidada automaticamente
- validar integridade entre jogadores, partidas e atuacoes
- expor ranking, comparacao e time ideal sem exigir uso de codigo

## Fluxo operacional da comissao

1. cadastrar os jogadores do elenco
2. cadastrar cada partida
3. lancar a atuacao de cada jogador por partida
4. consolidar automaticamente o elenco
5. consultar ranking por posicao, comparador 1x1 e time ideal

Regra central:

- o usuario informa fatos do jogo
- o sistema calcula metricas por 90, normaliza por posicao e gera os scores

Posicoes disponiveis na base atual:

- `Atacante`
- `Meia`
- `Zagueiro`
- `Lateral`
- `Volante`
- `Goleiro`

## Proximos passos

- substituir dados sinteticos por dados reais do elenco
- calibrar pesos com comissao tecnica e departamento medico
- incluir filtros por posicao, adversario e periodo da temporada
