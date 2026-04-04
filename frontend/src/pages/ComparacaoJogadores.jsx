import ComparacaoTable from "../components/ComparacaoTable";
import RadarComparacao from "../components/RadarComparacao";

export default function ComparacaoJogadores({ comparacao, comparacaoTexto, jogadoresOptions, jogadorLabelPorId, jogadorA, setJogadorA, jogadorB, setJogadorB }) {
  return (
    <div className="page-content">
      <section className="card">
        <div className="card-header spaced">
          <div>
            <span className="card-title">Comparar jogadores</span>
            <p className="section-copy">Disputa direta para titularidade e reserva imediato.</p>
          </div>
          <div className="toolbar">
            <select value={jogadorA} onChange={(e) => setJogadorA(e.target.value)}>
              {jogadoresOptions.map((j) => <option key={j.value} value={j.value}>{j.label}</option>)}
            </select>
            <select value={jogadorB} onChange={(e) => setJogadorB(e.target.value)}>
              {jogadoresOptions.map((j) => <option key={j.value} value={j.value}>{j.label}</option>)}
            </select>
          </div>
        </div>
        <div className="card-body">
          <ComparacaoTable comparacao={comparacao} />
        </div>
      </section>
      <section className="comp-radar-row">
        <RadarComparacao comparacao={comparacao} />
        <section className="insight-grid">
          {Object.entries(comparacaoTexto).map(([jogador, texto]) => (
            <article key={jogador} className="card">
              <div className="card-header"><span className="card-title">{jogadorLabelPorId[jogador] || jogador}</span></div>
              <div className="card-body"><p className="section-copy">{texto}</p></div>
            </article>
          ))}
        </section>
      </section>
    </div>
  );
}
