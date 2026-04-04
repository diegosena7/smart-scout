import { useState } from "react";
import DataTable from "../components/DataTable";
import EvolucaoJogador from "../components/EvolucaoJogador";
import { api } from "../api";

const COLUNAS_ANALISE = ["jogador", "posicao", "score_titularidade", "score_momento", "tendencia_recente", "rank_titularidade_posicao"];

export default function AnaliseElenco({ analise, atuacoes, partidas, jogadores, posicoesDisponiveis, filtroPosicao, setFiltroPosicao, loadAnalise, setFeedback }) {
  const [buscaJogador, setBuscaJogador] = useState("");
  const [resultadoBusca, setResultadoBusca] = useState([]);
  const [explicacaoBusca, setExplicacaoBusca] = useState("");

  async function handleBuscar(e) {
    e.preventDefault();
    if (!buscaJogador.trim()) return;
    try {
      const data = await api.getAnaliseJogador(buscaJogador.trim());
      setResultadoBusca(data.items);
      setExplicacaoBusca(data.explicacao);
    } catch (err) {
      setFeedback(err.message);
    }
  }

  function limpar() {
    setBuscaJogador("");
    setResultadoBusca([]);
    setExplicacaoBusca("");
  }

  return (
    <div className="page-content">
      <section className="card">
        <div className="card-header spaced">
          <div>
            <span className="card-title">Consultar jogador</span>
            <p className="section-copy">Busque pelo nome para ver a leitura individual.</p>
          </div>
        </div>
        <div className="card-body">
          <form className="inline-form" onSubmit={handleBuscar}>
            <input value={buscaJogador} onChange={(e) => setBuscaJogador(e.target.value)} placeholder="Nome do jogador" />
            <button className="btn-primary" type="submit">Consultar</button>
            {resultadoBusca.length > 0 && (
              <button className="btn-secondary" type="button" onClick={limpar}>Limpar</button>
            )}
          </form>
          {resultadoBusca.length > 0 && (
            <div className="stack-gap">
              <DataTable rows={resultadoBusca} preferredOrder={COLUNAS_ANALISE} dense />
              {explicacaoBusca && <div className="insight-block">{explicacaoBusca}</div>}
              {resultadoBusca.length === 1 && (
                <EvolucaoJogador
                  atuacoes={atuacoes}
                  partidas={partidas}
                  jogadorId={resultadoBusca[0].jogador_id}
                  jogadorNome={resultadoBusca[0].jogador}
                  posicao={resultadoBusca[0].posicao}
                />
              )}
            </div>
          )}
        </div>
      </section>

      <section className="card">
        <div className="card-header spaced">
          <div>
            <span className="card-title">Analise do elenco</span>
            <p className="section-copy">Forca na posicao e momento de cada atleta. Ranking calculado dentro da propria posicao.</p>
          </div>
          <div className="toolbar">
            <select value={filtroPosicao} onChange={(e) => setFiltroPosicao(e.target.value)}>
              <option value="">Todas as posicoes</option>
              {posicoesDisponiveis.map((p) => <option key={p} value={p}>{p}</option>)}
            </select>
            <button className="btn-secondary" onClick={() => loadAnalise(filtroPosicao).catch((err) => setFeedback(err.message))}>
              Atualizar
            </button>
          </div>
        </div>
        <div className="card-body">
          {analise.length === 0 && jogadores.length > 0
            ? (
              <div className="empty-state">
                Nenhum jogador com atuacoes lancadas ainda.
                Lance atuacoes em <strong>Entrada de dados → Atuacoes</strong> para ver a analise aqui.
              </div>
            )
            : <DataTable rows={analise} preferredOrder={COLUNAS_ANALISE} />
          }
        </div>
      </section>
    </div>
  );
}
