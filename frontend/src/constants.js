export const TABS = [
  { id: "dashboard", label: "Dashboard", icon: "⬡" },
  { id: "analise", label: "Analise do elenco", icon: "✦" },
  { id: "comparacao", label: "Comparar jogadores", icon: "⊞" },
  { id: "time", label: "Time ideal", icon: "◈" },
  { id: "entrada", label: "Entrada de dados", icon: "＋" },
];

export const ENTRY_TABS = [
  { id: "jogadores", label: "Elenco" },
  { id: "partidas", label: "Partidas" },
  { id: "atuacoes", label: "Atuacoes" },
];

export const DEFAULT_POSICOES = ["Atacante", "Meia", "Zagueiro", "Lateral", "Volante", "Goleiro"];
export const DEFAULT_RESULTADOS = ["Vitoria", "Empate", "Derrota"];
export const DEFAULT_FORMACOES = ["4-3-3", "4-4-2", "4-2-3-1"];
export const DEFAULT_FUNCOES = [
  "Centroavante", "Falso 9", "Segundo atacante", "Ponta direita", "Ponta esquerda",
  "Meia-atacante", "Meia central", "Volante construtor", "Volante marcador",
  "Ala-direito", "Ala-esquerdo", "Lateral ofensivo", "Lateral equilibrado",
  "Zagueiro construtor", "Zagueiro de cobertura", "Goleiro linha", "Goleiro classico",
];
export const DEFAULT_PES = ["Direito", "Esquerdo", "Ambidestro"];

export const emptyJogador = {
  jogador: "", posicao: "Atacante", status: "Ativo",
  funcao: "Centroavante", pe_dominante: "Direito", observacoes: "",
};
export const emptyPartida = {
  data_partida: "", adversario: "", competicao: "",
  mandante: "Casa", resultado: "Vitoria", formacao: "4-3-3", observacoes: "",
};
export const emptyAtuacao = {
  partida_id: "", jogador_id: "", jogador: "", posicao_jogo: "Atacante", minutos_jogados: 90,
  gols: 0, assistencias: 0, finalizacoes: 0, finalizacoes_no_alvo: 0, passes_decisivos: 0,
  passes_certos: 0, desarmes: 0, duelos_ganhos: 0, interceptacoes: 0, recuperacoes_bola: 0,
  cruzamentos_certos: 0, defesas_dificeis: 0, gols_sofridos: 0, jogos_sem_sofrer_gols: 0,
  cartoes_amarelos: 0, cartoes_vermelhos: 0, faltas_cometidas: 0,
};

export const METRICAS_POR_POSICAO_UI = {
  Atacante: ["gols", "assistencias", "finalizacoes", "finalizacoes_no_alvo", "passes_decisivos"],
  Meia: ["gols", "assistencias", "passes_certos", "passes_decisivos"],
  Volante: ["gols", "assistencias", "desarmes", "interceptacoes", "recuperacoes_bola", "passes_certos"],
  Lateral: ["gols", "assistencias", "desarmes", "interceptacoes", "cruzamentos_certos", "passes_decisivos"],
  Zagueiro: ["gols", "assistencias", "desarmes", "interceptacoes", "duelos_ganhos", "passes_certos"],
  Goleiro: ["defesas_dificeis", "gols_sofridos", "jogos_sem_sofrer_gols"],
};

export const QUICK_EVENTS_POR_POSICAO = [
  { label: "Gol marcado", hint: "impacto ofensivo", value: "gols", icon: "G", tone: "green", posicoes: ["Atacante", "Meia", "Volante", "Lateral", "Zagueiro"] },
  { label: "Assistencia", hint: "criacao decisiva", value: "assistencias", icon: "A", tone: "green", posicoes: ["Atacante", "Meia", "Volante", "Lateral", "Zagueiro"] },
  { label: "Passe decisivo", hint: "chance criada", value: "passes_decisivos", icon: "P", tone: "blue", posicoes: ["Atacante", "Meia", "Lateral"] },
  { label: "Desarme", hint: "acao defensiva", value: "desarmes", icon: "D", tone: "amber", posicoes: ["Volante", "Lateral", "Zagueiro"] },
  { label: "Defesa dificil", hint: "intervencao decisiva", value: "defesas_dificeis", icon: "DD", tone: "blue", posicoes: ["Goleiro"] },
  { label: "Cartao amarelo", hint: "disciplina", value: "cartoes_amarelos", icon: "CA", tone: "danger", posicoes: DEFAULT_POSICOES },
];

export const COLUMN_LABELS = {
  jogador_id: "ID do jogador",
  jogador: "Jogador",
  posicao: "Posicao",
  posicao_jogo: "Posicao no jogo",
  score_desempenho: "Desempenho",
  score_disciplina: "Disciplina",
  score_titularidade: "Forca na posicao",
  score_uso_recente: "Uso recente",
  score_momento: "Momento atual",
  rank_titularidade_posicao: "Ranking na posicao",
  rank_momento_posicao: "Ranking de momento",
  tendencia_recente: "Tendencia",
  partida_id: "Partida",
  minutos_jogados: "Minutos",
  assistencias: "Assistencias",
  gols: "Gols",
  vaga_formacao: "Vaga",
};

export const RADAR_METRICS = [
  { key: "score_desempenho", label: "Desempenho" },
  { key: "score_disciplina", label: "Disciplina" },
  { key: "score_titularidade", label: "Forca" },
  { key: "score_uso_recente", label: "Uso recente" },
  { key: "score_momento", label: "Momento" },
];

export const COMP_METRICS = [
  "score_desempenho", "score_disciplina", "score_titularidade", "score_momento",
];

export const EVOLUCAO_METRICAS = {
  Atacante: [
    { key: "gols", color: "#2ecc71", label: "Gols" },
    { key: "assistencias", color: "#4a9eff", label: "Assistencias" },
    { key: "finalizacoes_no_alvo", color: "#b28cff", label: "Fin. no alvo" },
  ],
  Meia: [
    { key: "assistencias", color: "#4a9eff", label: "Assistencias" },
    { key: "passes_decisivos", color: "#b28cff", label: "Passes decisivos" },
    { key: "gols", color: "#2ecc71", label: "Gols" },
  ],
  Volante: [
    { key: "desarmes", color: "#f0a500", label: "Desarmes" },
    { key: "interceptacoes", color: "#2ecc71", label: "Interceptacoes" },
    { key: "recuperacoes_bola", color: "#4a9eff", label: "Recuperacoes" },
  ],
  Lateral: [
    { key: "desarmes", color: "#f0a500", label: "Desarmes" },
    { key: "cruzamentos_certos", color: "#4a9eff", label: "Cruzamentos" },
    { key: "passes_decisivos", color: "#b28cff", label: "Passes decisivos" },
  ],
  Zagueiro: [
    { key: "desarmes", color: "#f0a500", label: "Desarmes" },
    { key: "duelos_ganhos", color: "#2ecc71", label: "Duelos ganhos" },
    { key: "interceptacoes", color: "#4a9eff", label: "Interceptacoes" },
  ],
  Goleiro: [
    { key: "defesas_dificeis", color: "#4a9eff", label: "Defesas dificeis" },
    { key: "gols_sofridos", color: "#e05555", label: "Gols sofridos" },
  ],
};
