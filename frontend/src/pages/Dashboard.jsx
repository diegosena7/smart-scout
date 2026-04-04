import PitchView from "../components/PitchView";
import PlayerRows from "../components/PlayerRows";
import DataTable from "../components/DataTable";

export default function Dashboard({ dashboard, timeIdeal, formacaoAtual, onNavigate }) {
  const cards = dashboard?.cards || {};
  return (
    <div className="page-content">
      <section className="hero-panel hero-panel-compact">
        <div>
          <span className="panel-kicker">Visao geral do elenco</span>
          <h2>Leitura operacional para decisao tecnica.</h2>
        </div>
        <div className="hero-actions">
          <span className="status-chip"><span className="status-dot" />Base atualizada</span>
          <button className="topbar-btn" onClick={() => onNavigate("atuacoes")}>+ Lancar atuacao</button>
        </div>
      </section>

      <section className="dashboard-hero-grid">
        <PitchView items={timeIdeal} formacao={formacaoAtual} compact />
        <div className="dashboard-side-stack">
          <article className="card">
            <div className="card-header spaced">
              <span className="card-title">Titulares recomendados</span>
              <span className="card-badge">{formacaoAtual}</span>
            </div>
            <PlayerRows items={dashboard?.titulares_recomendados || []} />
          </article>
          <article className="card">
            <div className="card-header"><span className="card-title">Destaques de momento</span></div>
            <PlayerRows items={dashboard?.destaques_momento || []} scoreField="score_momento" />
          </article>
        </div>
      </section>

      <section className="stats-row">
        <article className="stat-card">
          <span className="stat-label">Jogadores ativos</span>
          <strong className="stat-val">{cards.jogadores_ativos || 0}</strong>
          <small className="stat-sub">Elenco em operacao</small>
        </article>
        <article className="stat-card">
          <span className="stat-label">Partidas registradas</span>
          <strong className="stat-val">{cards.partidas_registradas || 0}</strong>
          <small className="stat-sub">Base competitiva</small>
        </article>
        <article className="stat-card">
          <span className="stat-label">Atuacoes lancadas</span>
          <strong className="stat-val">{cards.atuacoes_lancadas || 0}</strong>
          <small className="stat-sub">Rotina alimentada</small>
        </article>
        <article className="stat-card">
          <span className="stat-label">Modo analitico</span>
          <strong className="stat-val stat-accent">{cards.modo_analitico || "-"}</strong>
          <small className="stat-sub">Fonte de consolidacao</small>
        </article>
      </section>

      <section className="quick-actions">
        <button className="action-tile" onClick={() => onNavigate("jogadores")}>
          <span className="action-tile-icon">＋</span>
          <div><strong>Cadastrar jogador</strong><small>Entrada base do elenco</small></div>
        </button>
        <button className="action-tile" onClick={() => onNavigate("partidas")}>
          <span className="action-tile-icon">◷</span>
          <div><strong>Cadastrar partida</strong><small>Registro semanal da comissao</small></div>
        </button>
        <button className="action-tile primary" onClick={() => onNavigate("atuacoes")}>
          <span className="action-tile-icon">⚽</span>
          <div><strong>Lancar atuacao</strong><small>Fluxo operacional principal</small></div>
        </button>
      </section>

      <section className="dashboard-secondary-grid">
        <article className="card">
          <div className="card-header"><span className="card-title">Disputas por posicao</span></div>
          <div className="card-body">
            <DataTable
              rows={dashboard?.disputas_posicao || []}
              preferredOrder={["jogador", "posicao", "score_titularidade", "rank_titularidade_posicao"]}
              dense
            />
          </div>
        </article>
        <article className="card">
          <div className="card-header"><span className="card-title">Ultimos lancamentos</span></div>
          <div className="card-body">
            <DataTable
              rows={dashboard?.ultimas_atuacoes || []}
              preferredOrder={["partida_id", "jogador", "posicao_jogo", "minutos_jogados", "gols", "assistencias"]}
              dense
            />
          </div>
        </article>
      </section>
    </div>
  );
}
