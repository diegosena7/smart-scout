import { COMP_METRICS, COLUMN_LABELS } from "../constants";
import { displayName, trendData } from "../utils";

function cellClass(own, other) {
  if (own > other) return "comp-winner";
  if (own < other) return "comp-loser";
  return "";
}

export default function ComparacaoTable({ comparacao }) {
  if (!comparacao || comparacao.length < 2) return <div className="empty-state">Nenhum dado disponivel.</div>;
  const [a, b] = comparacao;
  const nomeA = displayName(a.jogador);
  const nomeB = displayName(b.jogador);
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>Metrica</th>
            <th style={{ color: "#2ecc71" }}>{nomeA}</th>
            <th style={{ color: "#4a9eff" }}>{nomeB}</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Posicao</td>
            <td>{a.posicao}</td>
            <td>{b.posicao}</td>
          </tr>
          {COMP_METRICS.map((key) => {
            const va = Number(a[key] || 0);
            const vb = Number(b[key] || 0);
            const classA = cellClass(va, vb);
            const classB = cellClass(vb, va);
            return (
              <tr key={key}>
                <td>{COLUMN_LABELS[key] || key}</td>
                <td className={classA}>{Math.round(va * 10) / 10}</td>
                <td className={classB}>{Math.round(vb * 10) / 10}</td>
              </tr>
            );
          })}
          <tr>
            <td>Tendencia</td>
            <td className={`trend ${trendData(a.tendencia_recente).className}`}>{trendData(a.tendencia_recente).symbol} {a.tendencia_recente || "-"}</td>
            <td className={`trend ${trendData(b.tendencia_recente).className}`}>{trendData(b.tendencia_recente).symbol} {b.tendencia_recente || "-"}</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}
