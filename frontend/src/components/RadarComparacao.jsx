import { RadarChart, PolarGrid, PolarAngleAxis, Radar, Legend, ResponsiveContainer } from "recharts";
import { RADAR_METRICS } from "../constants";
import { displayName } from "../utils";

export default function RadarComparacao({ comparacao }) {
  if (!comparacao || comparacao.length < 2) return null;
  const [a, b] = comparacao;
  const nomeA = displayName(a.jogador);
  const nomeB = displayName(b.jogador);
  const data = RADAR_METRICS.map((m) => ({
    metric: m.label,
    [nomeA]: Math.round(Number(a[m.key] || 0)),
    [nomeB]: Math.round(Number(b[m.key] || 0)),
  }));
  return (
    <article className="card">
      <div className="card-header">
        <span className="card-title">Radar comparativo</span>
        <span className="card-badge">{a.posicao}</span>
      </div>
      <div className="card-body radar-wrap">
        <ResponsiveContainer width="100%" height={300}>
          <RadarChart data={data} margin={{ top: 10, right: 40, bottom: 10, left: 40 }}>
            <PolarGrid stroke="rgba(255,255,255,0.08)" />
            <PolarAngleAxis dataKey="metric" tick={{ fill: "#9ab09e", fontSize: 11, fontWeight: 600 }} />
            <Radar name={nomeA} dataKey={nomeA} stroke="#2ecc71" fill="#2ecc71" fillOpacity={0.18} strokeWidth={2} dot={{ r: 3, fill: "#2ecc71" }} />
            <Radar name={nomeB} dataKey={nomeB} stroke="#4a9eff" fill="#4a9eff" fillOpacity={0.18} strokeWidth={2} dot={{ r: 3, fill: "#4a9eff" }} />
            <Legend wrapperStyle={{ color: "#9ab09e", fontSize: "0.78rem", paddingTop: "8px" }} />
          </RadarChart>
        </ResponsiveContainer>
      </div>
    </article>
  );
}
