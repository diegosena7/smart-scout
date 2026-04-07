import PitchView from "../components/PitchView";
import DataTable from "../components/DataTable";

export default function TimeIdeal({ timeIdeal, banco, formacaoAtual, setFormacaoAtual, formacoesDisponiveis }) {
  return (
    <div className="page-content">
<section className="card">
        <div className="card-header spaced">
          <div>
            <span className="card-title">Time ideal</span>
            <p className="section-copy">XI automatizado a partir do score de titularidade por posicao.</p>
          </div>
          <div className="toolbar">
            <select value={formacaoAtual} onChange={(e) => setFormacaoAtual(e.target.value)}>
              {formacoesDisponiveis.map((f) => <option key={f} value={f}>{f}</option>)}
            </select>
          </div>
        </div>
      </section>
      <section className="time-layout">
        <PitchView items={timeIdeal} banco={banco} formacao={formacaoAtual} />
        <article className="card">
          <div className="card-header"><span className="card-title">Leitura da escalacao</span></div>
          <div className="card-body">
            <DataTable
              rows={timeIdeal}
              preferredOrder={["jogador", "posicao", "vaga_formacao", "score_desempenho", "score_disciplina", "score_titularidade", "score_momento"]}
            />
          </div>
        </article>
      </section>
    </div>
  );
}
