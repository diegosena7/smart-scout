const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://127.0.0.1:8000";

async function request(path, options = {}) {
  let response;
  try {
    response = await fetch(`${API_BASE}${path}`, {
      headers: {
        "Content-Type": "application/json",
        ...(options.headers || {}),
      },
      ...options,
    });
  } catch (_error) {
    throw new Error(`Nao foi possivel conectar com a API em ${API_BASE}. Verifique se o backend FastAPI esta em execucao.`);
  }

  const isJson = response.headers.get("content-type")?.includes("application/json");
  const data = isJson ? await response.json() : null;

  if (!response.ok) {
    const detail = data?.detail;
    const message =
      typeof detail === "string"
        ? detail
        : detail?.erros?.join("\n") || `A API respondeu com erro ${response.status}.`;
    throw new Error(message);
  }

  return data;
}

export const api = {
  getContexto: () => request("/meta/contexto"),
  getDashboard: () => request("/dashboard/overview"),
  getJogadores: () => request("/jogadores"),
  createJogador: (payload) =>
    request("/jogadores", {
      method: "POST",
      body: JSON.stringify(payload),
    }),
  getPartidas: () => request("/partidas"),
  createPartida: (payload) =>
    request("/partidas", {
      method: "POST",
      body: JSON.stringify(payload),
    }),
  getAtuacoes: () => request("/atuacoes"),
  createAtuacao: (payload) =>
    request("/atuacoes", {
      method: "POST",
      body: JSON.stringify(payload),
    }),
  updateAtuacao: (atuacaoId, payload) =>
    request(`/atuacoes/${encodeURIComponent(atuacaoId)}`, {
      method: "PUT",
      body: JSON.stringify(payload),
    }),
  deleteAtuacao: (atuacaoId) =>
    request(`/atuacoes/${encodeURIComponent(atuacaoId)}`, {
      method: "DELETE",
    }),
  getAnaliseElenco: (posicao) => request(`/analise/elenco${posicao ? `?posicao=${encodeURIComponent(posicao)}` : ""}`),
  getAnaliseJogador: (nome) => request(`/analise/jogador/${encodeURIComponent(nome)}`),
  getComparacao: (jogadorA, jogadorB) =>
    request(`/analise/comparar?jogador_a=${encodeURIComponent(jogadorA)}&jogador_b=${encodeURIComponent(jogadorB)}`),
  getTimeIdeal: (formacao) => request(`/analise/time-ideal?formacao=${encodeURIComponent(formacao)}`),
  getValidacao: () => request("/analise/validacao"),
};
