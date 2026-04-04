import { COLUMN_LABELS } from "../constants";

export default function DataTable({ rows, preferredOrder, dense = false, columnLabels = COLUMN_LABELS }) {
  if (!rows?.length) return <div className="empty-state">Nenhum dado disponivel.</div>;
  const columns = preferredOrder?.filter((col) => col in rows[0]) || Object.keys(rows[0]);
  return (
    <div className={`table-wrap ${dense ? "dense" : ""}`}>
      <table>
        <thead>
          <tr>{columns.map((col) => <th key={col}>{columnLabels[col] || col}</th>)}</tr>
        </thead>
        <tbody>
          {rows.map((row, i) => (
            <tr key={`${i}-${row.jogador_id || row.jogador || row.partida_id || row.atuacao_id || "row"}`}>
              {columns.map((col) => <td key={col}>{row[col] ?? "-"}</td>)}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
