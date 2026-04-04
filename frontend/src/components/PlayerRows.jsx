import { initials, shortPos, posTone, trendData } from "../utils";

export default function PlayerRows({ items, scoreField = "score_titularidade" }) {
  return (
    <div className="card-body flush">
      {items.map((item, i) => {
        const trend = trendData(item.tendencia_recente);
        const score = Math.round(Number(item[scoreField] || 0));
        return (
          <div key={`${item.jogador_id || item.jogador}-${item.posicao}-${i}`} className="player-row-rich">
            <div className="player-rank">{item.rank_titularidade_posicao || i + 1}</div>
            <div className="player-avatar">{initials(item.jogador)}</div>
            <div className="player-main">
              <strong>{item.jogador}</strong>
              <div className="player-meta">
                <span className={`pos-pill pos-${shortPos(item.posicao)}`}>{shortPos(item.posicao)}</span>
                <span>{item.posicao}</span>
              </div>
            </div>
            <div className="score-bar-wrap">
              <div className="score-bar-track">
                <div className={`score-bar-fill ${posTone(item.posicao)}`} style={{ width: `${score}%` }} />
              </div>
              <div className="score-num">{score}</div>
            </div>
            <div className={`trend ${trend.className}`}>{trend.symbol}</div>
          </div>
        );
      })}
    </div>
  );
}
