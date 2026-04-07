import { useEffect, useMemo, useState } from "react";
import { api } from "./api";
import {
  TABS, DEFAULT_POSICOES, DEFAULT_RESULTADOS, DEFAULT_FORMACOES, DEFAULT_FUNCOES, DEFAULT_PES,
  METRICAS_POR_POSICAO_UI, QUICK_EVENTS_POR_POSICAO,
  emptyJogador, emptyPartida, emptyAtuacao,
} from "./constants";
import Dashboard from "./pages/Dashboard";
import EntradaDados from "./pages/EntradaDados";
import AnaliseElenco from "./pages/AnaliseElenco";
import ComparacaoJogadores from "./pages/ComparacaoJogadores";
import TimeIdeal from "./pages/TimeIdeal";

function App() {
  const [activeTab, setActiveTab] = useState("dashboard");
  const [entryTab, setEntryTab] = useState("atuacoes");
  const [contexto, setContexto] = useState(null);
  const [dashboard, setDashboard] = useState(null);
  const [jogadores, setJogadores] = useState([]);
  const [partidas, setPartidas] = useState([]);
  const [atuacoes, setAtuacoes] = useState([]);
  const [analise, setAnalise] = useState([]);
  const [comparacao, setComparacao] = useState([]);
  const [comparacaoTexto, setComparacaoTexto] = useState({});
  const [timeIdeal, setTimeIdeal] = useState([]);
  const [timeIdealBanco, setTimeIdealBanco] = useState([]);
  const [filtroPosicao, setFiltroPosicao] = useState("");
  const [formacaoAtual, setFormacaoAtual] = useState("4-3-3");
  const [jogadorForm, setJogadorForm] = useState(emptyJogador);
  const [partidaForm, setPartidaForm] = useState(emptyPartida);
  const [atuacaoForm, setAtuacaoForm] = useState(emptyAtuacao);
  const [atuacaoEmEdicaoId, setAtuacaoEmEdicaoId] = useState("");
  const [jogadorEmEdicaoId, setJogadorEmEdicaoId] = useState("");
  const [jogadorA, setJogadorA] = useState("");
  const [jogadorB, setJogadorB] = useState("");
  const [feedback, setFeedback] = useState("");
  const [toast, setToast] = useState("");
  const [loading, setLoading] = useState(true);

  function showToast(msg) {
    setToast(msg);
    setTimeout(() => setToast(""), 2500);
  }

  const posicoesDisponiveis = contexto?.posicoes_validas?.length ? contexto.posicoes_validas : DEFAULT_POSICOES;
  const resultadosDisponiveis = contexto?.resultados_validos?.length ? contexto.resultados_validos : DEFAULT_RESULTADOS;
  const formacoesDisponiveis = contexto?.formacoes_disponiveis?.length ? contexto.formacoes_disponiveis : DEFAULT_FORMACOES;
  const funcoesDisponiveis = contexto?.funcoes_taticas_validas?.length ? contexto.funcoes_taticas_validas : DEFAULT_FUNCOES;
  const pesDisponiveis = contexto?.pes_dominantes_validos?.length ? contexto.pes_dominantes_validos : DEFAULT_PES;

  const jogadoresOptions = useMemo(() => jogadores.map((j) => ({
    value: j.jogador_id, label: `${j.jogador} · ${j.posicao}`, nome: j.jogador, posicao: j.posicao,
  })), [jogadores]);
  const jogadorLabelPorId = useMemo(() => Object.fromEntries(jogadoresOptions.map((j) => [j.value, j.label])), [jogadoresOptions]);
  const camposAtuacaoVisiveis = useMemo(() => [
    ...(METRICAS_POR_POSICAO_UI[atuacaoForm.posicao_jogo] || []),
    "cartoes_amarelos", "cartoes_vermelhos", "faltas_cometidas",
  ], [atuacaoForm.posicao_jogo]);
  const eventosRapidosVisiveis = useMemo(
    () => QUICK_EVENTS_POR_POSICAO.filter((e) => e.posicoes.includes(atuacaoForm.posicao_jogo)),
    [atuacaoForm.posicao_jogo],
  );
  const navBadges = useMemo(() => ({
    dashboard: dashboard?.cards?.jogadores_ativos || 0,
    analise: analise.length,
    comparacao: comparacao.length,
    time: timeIdeal.length,
    entrada: jogadores.length + partidas.length + atuacoes.length,
  }), [dashboard, analise, comparacao, timeIdeal, jogadores, partidas, atuacoes]);

  async function loadBase() {
    const [ctx, dash, jogs, parts, atuas] = await Promise.all([
      api.getContexto(), api.getDashboard(), api.getJogadores(), api.getPartidas(), api.getAtuacoes(),
    ]);
    setContexto(ctx);
    setDashboard(dash);
    setJogadores(jogs.items);
    setPartidas(parts.items);
    setAtuacoes(atuas.items);
    setJogadorA((cur) => cur || jogs.items[0]?.jogador_id || "");
    setJogadorB((cur) => cur || jogs.items[1]?.jogador_id || jogs.items[0]?.jogador_id || "");
    setJogadorForm((cur) => ({ ...cur, posicao: cur.posicao || ctx.posicoes_validas?.[0] || DEFAULT_POSICOES[0], funcao: cur.funcao || ctx.funcoes_taticas_validas?.[0] || DEFAULT_FUNCOES[0], pe_dominante: cur.pe_dominante || ctx.pes_dominantes_validos?.[0] || DEFAULT_PES[0] }));
    setPartidaForm((cur) => ({ ...cur, resultado: cur.resultado || ctx.resultados_validos?.[0] || DEFAULT_RESULTADOS[0], formacao: cur.formacao || ctx.formacoes_disponiveis?.[0] || DEFAULT_FORMACOES[0] }));
    setAtuacaoForm((cur) => ({ ...cur, partida_id: cur.partida_id || parts.items[0]?.partida_id || "", jogador_id: cur.jogador_id || jogs.items[0]?.jogador_id || "", jogador: cur.jogador || jogs.items[0]?.jogador || "", posicao_jogo: cur.posicao_jogo || jogs.items[0]?.posicao || ctx.posicoes_validas?.[0] || DEFAULT_POSICOES[0] }));
  }

  async function loadAnalise(posicao = filtroPosicao) {
    const data = await api.getAnaliseElenco(posicao || undefined);
    setAnalise(data.items);
  }

  async function loadComparacao(a = jogadorA, b = jogadorB) {
    if (!a || !b) return;
    const data = await api.getComparacao(a, b);
    setComparacao(data.items);
    setComparacaoTexto(data.explicacoes);
  }

  async function loadTimeIdeal(formacao = formacaoAtual) {
    const data = await api.getTimeIdeal(formacao);
    setTimeIdeal(data.items);
    setTimeIdealBanco(data.banco || []);
  }

  async function bootstrap() {
    setLoading(true);
    try {
      await loadBase();
      await Promise.all([loadAnalise(""), loadTimeIdeal(formacaoAtual)]);
    } finally {
      setLoading(false);
    }
  }

  async function refreshAnalise(posicao) {
    await Promise.all([loadBase(), loadAnalise(posicao)]);
  }

  useEffect(() => { bootstrap().catch((err) => { setFeedback(err.message); setLoading(false); }); }, []);
  useEffect(() => { if (jogadorA && jogadorB) loadComparacao(jogadorA, jogadorB).catch((err) => setFeedback(err.message)); }, [jogadorA, jogadorB]);
  useEffect(() => { loadTimeIdeal(formacaoAtual).catch((err) => setFeedback(err.message)); }, [formacaoAtual]);
  useEffect(() => { setFeedback(""); }, [activeTab]);

  async function submitJogador(e) {
    e.preventDefault();
    try {
      if (jogadorEmEdicaoId) {
        const data = await api.updateJogador(jogadorEmEdicaoId, jogadorForm);
        showToast(data.mensagem || "Jogador atualizado com sucesso.");
        setJogadorEmEdicaoId("");
      } else {
        const data = await api.createJogador(jogadorForm);
        showToast(data.mensagem);
        setActiveTab("dashboard");
      }
      setJogadorForm({ ...emptyJogador, posicao: posicoesDisponiveis[0], funcao: funcoesDisponiveis[0], pe_dominante: pesDisponiveis[0] });
      await bootstrap();
    } catch (err) { setFeedback(err.message); }
  }

  function iniciarEdicaoJogador(jogador) {
    setJogadorEmEdicaoId(jogador.jogador_id);
    setJogadorForm({
      jogador: jogador.jogador,
      posicao: jogador.posicao,
      funcao_tatica: jogador.funcao_tatica || "",
      pe_dominante: jogador.pe_dominante || "",
    });
    setActiveTab("entrada");
    setEntryTab("jogadores");
  }

  function cancelarEdicaoJogador() {
    setJogadorEmEdicaoId("");
    setJogadorForm({ ...emptyJogador, posicao: posicoesDisponiveis[0], funcao: funcoesDisponiveis[0], pe_dominante: pesDisponiveis[0] });
  }

  async function excluirJogadorLinha(jogadorId) {
    if (!window.confirm("Excluir este jogador? Esta acao nao pode ser desfeita.")) return;
    try {
      const data = await api.deleteJogador(jogadorId);
      showToast(data?.mensagem || "Jogador excluido com sucesso.");
      if (jogadorEmEdicaoId === jogadorId) cancelarEdicaoJogador();
      await bootstrap();
    } catch (err) { setFeedback(err.message); }
  }

  async function submitPartida(e) {
    e.preventDefault();
    try {
      const data = await api.createPartida(partidaForm);
      showToast(data.mensagem);
      setPartidaForm({ ...emptyPartida, resultado: resultadosDisponiveis[0], formacao: formacoesDisponiveis[0] });
      await bootstrap();
      setEntryTab("atuacoes");
      setActiveTab("dashboard");
    } catch (err) { setFeedback(err.message); }
  }

  async function submitAtuacao(e, onSuccess) {
    e.preventDefault();
    try {
      const data = atuacaoEmEdicaoId
        ? await api.updateAtuacao(atuacaoEmEdicaoId, atuacaoForm)
        : await api.createAtuacao(atuacaoForm);
      showToast(data.mensagem);
       // keep partida_id so user can enter next player without re-selecting the match
       setAtuacaoForm((cur) => ({ ...emptyAtuacao, partida_id: cur.partida_id, minutos_jogados: 90, presenca: "JOGOU", nota_tecnico: null }));
      setAtuacaoEmEdicaoId("");
      await bootstrap();
      if (onSuccess) onSuccess();
    } catch (err) { setFeedback(err.message); }
  }

  function iniciarEdicaoAtuacao(atuacao) {
    setAtuacaoEmEdicaoId(atuacao.atuacao_id);
    setAtuacaoForm({
      partida_id: atuacao.partida_id || "", jogador_id: atuacao.jogador_id || "", jogador: atuacao.jogador || "",
      posicao_jogo: atuacao.posicao_jogo || posicoesDisponiveis[0], minutos_jogados: Number(atuacao.minutos_jogados || 0),
      gols: Number(atuacao.gols || 0), assistencias: Number(atuacao.assistencias || 0), finalizacoes: Number(atuacao.finalizacoes || 0),
      finalizacoes_no_alvo: Number(atuacao.finalizacoes_no_alvo || 0), passes_decisivos: Number(atuacao.passes_decisivos || 0),
      passes_certos: Number(atuacao.passes_certos || 0), desarmes: Number(atuacao.desarmes || 0), duelos_ganhos: Number(atuacao.duelos_ganhos || 0),
      interceptacoes: Number(atuacao.interceptacoes || 0), recuperacoes_bola: Number(atuacao.recuperacoes_bola || 0),
      cruzamentos_certos: Number(atuacao.cruzamentos_certos || 0), defesas_dificeis: Number(atuacao.defesas_dificeis || 0),
      gols_sofridos: Number(atuacao.gols_sofridos || 0), jogos_sem_sofrer_gols: Number(atuacao.jogos_sem_sofrer_gols || 0),
      cartoes_amarelos: Number(atuacao.cartoes_amarelos || 0), cartoes_vermelhos: Number(atuacao.cartoes_vermelhos || 0),
      faltas_cometidas: Number(atuacao.faltas_cometidas || 0),
    });
    setActiveTab("entrada");
    setEntryTab("atuacoes");
  }

  function cancelarEdicaoAtuacao() {
    setAtuacaoEmEdicaoId("");
    setAtuacaoForm({ ...emptyAtuacao, partida_id: partidas[0]?.partida_id || "", jogador_id: jogadores[0]?.jogador_id || "", jogador: jogadores[0]?.jogador || "", posicao_jogo: jogadores[0]?.posicao || posicoesDisponiveis[0], minutos_jogados: 90 });
  }

  function selecionarJogadorAtuacao(jogadorId) {
    const sel = jogadores.find((j) => j.jogador_id === jogadorId);
    setAtuacaoForm((cur) => ({ ...cur, jogador_id: jogadorId, jogador: sel?.jogador || "", posicao_jogo: sel?.posicao || cur.posicao_jogo || posicoesDisponiveis[0] }));
  }

  function incrementarEvento(field) {
    setAtuacaoForm((cur) => ({ ...cur, [field]: Number(cur[field] || 0) + 1 }));
  }

  async function excluirAtuacaoLinha(atuacaoId) {
    if (!window.confirm("Excluir esta atuacao? A analise do elenco sera recalculada.")) return;
    try {
      const data = await api.deleteAtuacao(atuacaoId);
      setFeedback(data.mensagem);
      if (atuacaoEmEdicaoId === atuacaoId) cancelarEdicaoAtuacao();
      await bootstrap();
    } catch (err) { setFeedback(err.message); }
  }

  function irParaEntrada(subtab) {
    setActiveTab("entrada");
    setEntryTab(subtab);
  }

  if (loading) return <div className="loading-screen">Carregando SmartScout AI...</div>;

  return (
    <div className="app-shell">
      <div className={`page-bg-photo bg-${activeTab}`} aria-hidden="true" />
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-badge">Matchday Intelligence</div>
          <div className="brand-name">SmartScout AI</div>
        </div>
        <nav className="nav">
          {TABS.map((tab) => (
            <button key={tab.id} className={tab.id === activeTab ? "nav-item active" : "nav-item"} onClick={() => setActiveTab(tab.id)}>
              <span className="nav-icon">{tab.icon}</span>
              <span>{tab.label}</span>
              <span className="nav-badge">{navBadges[tab.id] || 0}</span>
            </button>
          ))}
        </nav>
        <div className="sidebar-footer">
          <div className="club-info"><strong>Base operacional</strong>{contexto?.modo_analitico || "-"}</div>
        </div>
      </aside>

      <main className="main">
        <div className="topbar">
          <span className="topbar-section">{TABS.find((t) => t.id === activeTab)?.label || "Dashboard"}</span>
          <span className="topbar-sep">·</span>
          <span className="topbar-page">SmartScout AI</span>
          <div className="topbar-right">
            <span className="status-chip"><span className="status-dot" />Base atualizada</span>
            <button className="topbar-btn" onClick={() => setActiveTab("dashboard")}>Home</button>
          </div>
        </div>
        {feedback && <div className="feedback-banner">{feedback}</div>}
        {toast && <div className="toast-success">{toast}</div>}
        <div className="content">
          {activeTab === "dashboard" && (
            <Dashboard
              dashboard={dashboard}
              timeIdeal={timeIdeal}
              banco={timeIdealBanco}
              formacaoAtual={formacaoAtual}
              setFormacaoAtual={setFormacaoAtual}
              formacoesDisponiveis={formacoesDisponiveis}
              onNavigate={irParaEntrada}
            />
          )}
          {activeTab === "analise" && (
            <AnaliseElenco
              analise={analise} atuacoes={atuacoes} partidas={partidas}
              jogadores={jogadores}
              posicoesDisponiveis={posicoesDisponiveis}
              filtroPosicao={filtroPosicao} setFiltroPosicao={setFiltroPosicao}
              loadAnalise={refreshAnalise} setFeedback={setFeedback}
            />
          )}
          {activeTab === "comparacao" && (
            <ComparacaoJogadores
              comparacao={comparacao} comparacaoTexto={comparacaoTexto}
              jogadoresOptions={jogadoresOptions} jogadorLabelPorId={jogadorLabelPorId}
              jogadorA={jogadorA} setJogadorA={setJogadorA}
              jogadorB={jogadorB} setJogadorB={setJogadorB}
            />
          )}
          {activeTab === "time" && (
            <TimeIdeal
              timeIdeal={timeIdeal}
              banco={timeIdealBanco}
              formacaoAtual={formacaoAtual}
              setFormacaoAtual={setFormacaoAtual}
              formacoesDisponiveis={formacoesDisponiveis}
            />
          )}
          {activeTab === "entrada" && (
            <EntradaDados
              entryTab={entryTab} setEntryTab={setEntryTab}
              jogadores={jogadores} partidas={partidas} atuacoes={atuacoes}
              posicoesDisponiveis={posicoesDisponiveis} resultadosDisponiveis={resultadosDisponiveis}
              formacoesDisponiveis={formacoesDisponiveis} funcoesDisponiveis={funcoesDisponiveis} pesDisponiveis={pesDisponiveis}
              jogadorForm={jogadorForm} setJogadorForm={setJogadorForm} submitJogador={submitJogador}
              jogadorEmEdicaoId={jogadorEmEdicaoId} iniciarEdicaoJogador={iniciarEdicaoJogador}
              cancelarEdicaoJogador={cancelarEdicaoJogador} excluirJogadorLinha={excluirJogadorLinha}
              partidaForm={partidaForm} setPartidaForm={setPartidaForm} submitPartida={submitPartida}
              atuacaoForm={atuacaoForm} setAtuacaoForm={setAtuacaoForm} atuacaoEmEdicaoId={atuacaoEmEdicaoId}
              submitAtuacao={submitAtuacao} cancelarEdicaoAtuacao={cancelarEdicaoAtuacao}
              selecionarJogadorAtuacao={selecionarJogadorAtuacao}
              incrementarEvento={incrementarEvento} excluirAtuacaoLinha={excluirAtuacaoLinha}
              iniciarEdicaoAtuacao={iniciarEdicaoAtuacao}
              jogadoresOptions={jogadoresOptions}
              camposAtuacaoVisiveis={camposAtuacaoVisiveis} eventosRapidosVisiveis={eventosRapidosVisiveis}
            />
          )}
        </div>
      </main>
    </div>
  );
}

export default App;
