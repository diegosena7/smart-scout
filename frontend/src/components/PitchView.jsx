import { initials, displayName, posTone } from "../utils";

export default function PitchView({ items, banco, formacao, compact = false, onFormacaoChange, formacoesDisponiveis }) {
  const rows = [
    { top: "10%", players: (items || []).filter((p) => p.vaga_formacao === "Atacante") },
    { top: "32%", players: (items || []).filter((p) => p.vaga_formacao === "Meia" || p.vaga_formacao === "Volante") },
    { top: "63%", players: (items || []).filter((p) => p.vaga_formacao === "Lateral" || p.vaga_formacao === "Zagueiro") },
    { top: "86%", players: (items || []).filter((p) => p.vaga_formacao === "Goleiro") },
  ];
  return (
    <article className="card">
      <div className="card-header spaced">
        <span className="card-title">Escalacao recomendada</span>
        {onFormacaoChange ? (
          <select value={formacao} onChange={(e) => onFormacaoChange(e.target.value)}>
            {(formacoesDisponiveis || [formacao]).map((f) => <option key={f} value={f}>{f}</option>)}
          </select>
        ) : (
          <span className="card-badge">{formacao}</span>
        )}
      </div>
      <div className={`pitch-field${compact ? " pitch-field-compact" : ""}`}>
        <div className="pitch-outline" />
        <div className="pitch-half" />
        <div className="pitch-circle" />
        {!items?.length && (
          <div style={{ position: "absolute", inset: 0, display: "grid", placeItems: "center", color: "var(--text3)", fontSize: "0.82rem" }}>
            Carregando escalacao...
          </div>
        )}
        {rows.map((row) => (
          <div key={row.top} className="pitch-row" style={{ top: row.top }}>
            {row.players.map((player) => (
              <div key={`${player.jogador_id || player.jogador}-${player.vaga_formacao}`} className="pitch-player-wrap">
                <div className={`pitch-player ${posTone(player.posicao)}`}>
                  <span>{initials(player.jogador)}</span>
                  <small>{Math.round(Number(player.score_titularidade || 0))}</small>
                </div>
                <div className="pitch-player-name">{displayName(player.jogador)}</div>
              </div>
            ))}
          </div>
        ))}
      </div>
      {banco && banco.length > 0 && (
        <div className="pitch-bench">
          <div className="pitch-bench-label">Banco</div>
          <div className="pitch-bench-players">
            {banco.map((player) => (
              <div key={player.jogador_id || player.jogador} className="pitch-player-wrap">
                <div className={`pitch-player ${posTone(player.posicao)}`}>
                  <span>{initials(player.jogador)}</span>
                  <small>{Math.round(Number(player.score_titularidade || 0))}</small>
                </div>
                <div className="pitch-player-name">{displayName(player.jogador)}</div>
              </div>
            ))}
          </div>
        </div>
      )}
    </article>
  );
}
