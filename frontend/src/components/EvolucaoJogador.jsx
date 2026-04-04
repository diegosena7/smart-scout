import { useMemo } from "react";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";
import { EVOLUCAO_METRICAS } from "../constants";

export default function EvolucaoJogador({ atuacoes, partidas, jogadorId, jogadorNome, posicao }) {
  const partidaMap = useMemo(
    () => Object.fromEntries(partidas.map((p) => [p.partida_id, p])),
    [partidas],
  );
  const playerAtuacoes = useMemo(
    () => atuacoes.filter((a) => (jogadorId && a.jogador_id === jogadorId) || (!jogadorId && a.jogador === jogadorNome)),
    [atuacoes, jogadorId, jogadorNome],
  );
  if (!playerAtuacoes.length) return null;
  const metrics = EVOLUCAO_METRICAS[posicao] || EVOLUCAO_METRICAS.Atacante;
  const chartData = playerAtuacoes
    .slice()
    .sort((x, y) => {
      const pa = partidaMap[x.partida_id]?.data_partida || "";
      const pb = partidaMap[y.partida_id]?.data_partida || "";
      return pa.localeCompare(pb);
    })
    .map((a) => {
      const partida = partidaMap[a.partida_id];
      const entry = { jogo: partida?.adversario || a.partida_id };
      metrics.forEach((m) => { entry[m.key] = Number(a[m.key] || 0); });
      return entry;
    });
  return (
    <article className="card">
      <div className="card-header">
        <span className="card-title">Evolucao por partida</span>
        <span className="card-badge">{chartData.length} jogos</span>
      </div>
      <div className="card-body evolucao-wrap">
        <ResponsiveContainer width="100%" height={220}>
          <BarChart data={chartData} margin={{ top: 4, right: 16, bottom: 4, left: -20 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" />
            <XAxis dataKey="jogo" tick={{ fill: "#9ab09e", fontSize: 10 }} />
            <YAxis tick={{ fill: "#9ab09e", fontSize: 10 }} allowDecimals={false} />
            <Tooltip
              contentStyle={{ background: "#132018", border: "1px solid rgba(255,255,255,0.08)", borderRadius: "8px", color: "#e8f0ea", fontSize: "0.82rem" }}
              cursor={{ fill: "rgba(255,255,255,0.04)" }}
            />
            <Legend wrapperStyle={{ color: "#9ab09e", fontSize: "0.78rem" }} />
            {metrics.map((m) => (
              <Bar key={m.key} dataKey={m.key} name={m.label} fill={m.color} radius={[3, 3, 0, 0]} maxBarSize={32} />
            ))}
          </BarChart>
        </ResponsiveContainer>
      </div>
    </article>
  );
}
