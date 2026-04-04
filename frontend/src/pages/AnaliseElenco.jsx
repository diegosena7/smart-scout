import { useState } from "react";
import DataTable from "../components/DataTable";
import EvolucaoJogador from "../components/EvolucaoJogador";
import { api } from "../api";

export default function AnaliseElenco({ analise, atuacoes, partidas, posicoesDisponiveis, filtroPosicao, setFiltroPosicao, loadAnalise, setFeedback }) {
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
            <span className="card-title">Consultar Jogador</span>
            <p className="section-copy">Busque pelo nome do atleta para ver a leitura individual dentro da disputa posicional.</p>
          </div>
        </div>
        <div className="card-body">
          <form className="inline-form" onSubmit={handleBuscar}>
            <input value={buscaJogador} onChange={(e) => setBuscaJogador(e.target.value)} placeholder="Digite o nome do jogador" />
            <button className="btn-primary" type="submit">Consultar</button>
            <button className="btn-secondary" type="button" onClick={limpar}>Limpar consulta</button>
          </form>
          <div className="stack-gap">
            <DataTable
              rows={resultadoBusca}
              preferredOrder={["jogador", "posicao", "score_titularidade", "score_momento", "rank_titularidade_posicao", "tendencia_recente"]}
              dense
            />
            {explicacaoBusca ? <div className="insight-block">{explicacaoBusca}</div> : null}
          </div>
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
      </section>

      <section className="card">
        <div className="card-header spaced">
          <div>
            <span className="card-title">Analise do elenco</span>
            <p className="section-copy">Compare atletas sempre dentro da propria disputa posicional.</p>
          </div>
          <div className="toolbar">
            <select value={filtroPosicao} onChange={(e) => setFiltroPosicao(e.target.value)}>
              <option value="">Todas as posicoes</option>
              {posicoesDisponiveis.map((p) => <option key={p} value={p}>{p}</option>)}
            </select>
            <button className="btn-secondary" onClick={() => loadAnalise(filtroPosicao).catch((err) => setFeedback(err.message))}>
              Atualizar analise
            </button>
          </div>
        </div>
        <div className="card-body">
          <DataTable
            rows={analise}
            preferredOrder={["jogador", "posicao", "score_desempenho", "score_disciplina", "score_titularidade", "score_momento", "rank_titularidade_posicao"]}
          />
        </div>
      </section>

      <section className="card">
        <div className="card-header"><span className="card-title">Como ler esta analise</span></div>
        <div className="card-body">
          <div className="insight-grid">
            <div className="insight-block">
              <strong>Desempenho</strong>
              <p className="section-copy">Score tecnico do atleta na propria posicao. O sistema transforma os dados do usuario em metricas por 90 e aplica a formula da funcao.</p>
            </div>
            <div className="insight-block">
              <strong>Disciplina</strong>
              <p className="section-copy">Penaliza amarelos, vermelhos e faltas. Quanto menor a indisciplina bruta, maior o score final.</p>
            </div>
            <div className="insight-block">
              <strong>Forca na posicao</strong>
              <p className="section-copy">Combina desempenho e disciplina. Hoje: 79% desempenho + 21% disciplina.</p>
            </div>
            <div className="insight-block">
              <strong>Momento atual</strong>
              <p className="section-copy">Ajusta a forca estrutural com o uso recente. Hoje: 70% forca na posicao + 30% uso recente.</p>
            </div>
            <div className="insight-block">
              <strong>Ranking na posicao</strong>
              <p className="section-copy">Mostra a colocacao do atleta dentro da disputa da propria posicao. 1 significa lider da posicao.</p>
            </div>
            <div className="insight-block">
              <strong>Calculo base</strong>
              <p className="section-copy">Exemplos: atacante usa gols, assistencias, finalizacoes no alvo e passes decisivos por 90. Zagueiro usa desarmes, interceptacoes, duelos e passe. Goleiro usa defesas dificeis, gols sofridos invertidos e jogos sem sofrer gols.</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
