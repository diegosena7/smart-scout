import { useState, useEffect } from "react";
import DataTable from "../components/DataTable";
import { ENTRY_TABS } from "../constants";
import { displayName, formatLabel, initials, shortPos, posTone } from "../utils";

function AtuacaoWizard({
  partidas, jogadores, atuacoes,
  atuacaoForm, setAtuacaoForm, atuacaoEmEdicaoId,
  submitAtuacao, cancelarEdicaoAtuacao,
  selecionarJogadorAtuacao, incrementarEvento, excluirAtuacaoLinha, iniciarEdicaoAtuacao,
  jogadoresOptions, camposAtuacaoVisiveis, eventosRapidosVisiveis,
  posicoesDisponiveis,
}) {
  const [step, setStep] = useState(atuacaoEmEdicaoId ? 3 : 1);
  const [showDetalhes, setShowDetalhes] = useState(false);

  // when editing starts externally, jump to step 3
  useEffect(() => {
    if (atuacaoEmEdicaoId) setStep(3);
  }, [atuacaoEmEdicaoId]);

  const partidaSelecionada = partidas.find((p) => p.partida_id === atuacaoForm.partida_id);
  const jogadoresAtivos = jogadores.filter((j) => j.status === "Ativo" || !j.status);

  function selecionarPartida(partidaId) {
    setAtuacaoForm((cur) => ({ ...cur, partida_id: partidaId }));
    setStep(2);
  }

  function selecionarJogador(jogadorId) {
    selecionarJogadorAtuacao(jogadorId);
    setStep(3);
    setShowDetalhes(false);
  }

  function handleSubmit(e) {
    submitAtuacao(e, () => {
      setStep(2); // keep match, pick next player
      setShowDetalhes(false);
    });
  }

  function handleCancelarEdicao() {
    cancelarEdicaoAtuacao();
    setStep(1);
  }

  // ── Step 1: pick match ─────────────────────────────────────────────────────
  if (step === 1) {
    return (
      <div className="wizard">
        <div className="wizard-steps">
          <div className="wstep active">1 Partida</div>
          <div className="wstep">2 Jogador</div>
          <div className="wstep">3 Stats</div>
        </div>
        <div className="wizard-title">Qual foi a partida?</div>
        {partidas.length === 0
          ? <div className="empty-state">Nenhuma partida cadastrada. Cadastre uma partida primeiro.</div>
          : (
            <div className="match-list">
              {partidas.slice().reverse().map((p) => (
                <button
                  key={p.partida_id}
                  className={`match-card ${atuacaoForm.partida_id === p.partida_id ? "selected" : ""}`}
                  onClick={() => selecionarPartida(p.partida_id)}
                >
                  <div className="match-card-main">
                    <span className="match-card-vs">vs {p.adversario}</span>
                    <span className="match-card-date">{p.data_partida}</span>
                  </div>
                  <span className={`match-card-result result-${(p.resultado || "").toLowerCase()}`}>
                    {p.resultado}
                  </span>
                </button>
              ))}
            </div>
          )
        }
      </div>
    );
  }

  // ── Step 2: pick player ────────────────────────────────────────────────────
  if (step === 2) {
    return (
      <div className="wizard">
        <div className="wizard-steps">
          <div className="wstep done" onClick={() => setStep(1)}>1 Partida</div>
          <div className="wstep active">2 Jogador</div>
          <div className="wstep">3 Stats</div>
        </div>
        <div className="wizard-context">
          <button className="wizard-back" onClick={() => setStep(1)}>← Trocar partida</button>
          {partidaSelecionada && (
            <span className="wizard-subtitle">vs {partidaSelecionada.adversario} · {partidaSelecionada.data_partida}</span>
          )}
        </div>
        <div className="wizard-title">Qual jogador?</div>
        {jogadoresAtivos.length === 0
          ? <div className="empty-state">Nenhum jogador ativo. Cadastre jogadores primeiro.</div>
          : (
            <div className="player-grid">
              {jogadoresAtivos.map((j) => (
                <button
                  key={j.jogador_id}
                  className={`player-select-card ${atuacaoForm.jogador_id === j.jogador_id ? "selected" : ""} ${posTone(j.posicao)}`}
                  onClick={() => selecionarJogador(j.jogador_id)}
                >
                  <div className="player-select-avatar">{initials(j.jogador)}</div>
                  <div className="player-select-name">{displayName(j.jogador)}</div>
                  <span className={`pos-pill pos-${shortPos(j.posicao)}`}>{shortPos(j.posicao)}</span>
                </button>
              ))}
            </div>
          )
        }
      </div>
    );
  }

  // ── Step 3: stats entry ────────────────────────────────────────────────────
  return (
    <div className="wizard">
      <div className="wizard-steps">
        <div className="wstep done" onClick={() => setStep(1)}>1 Partida</div>
        <div className="wstep done" onClick={() => setStep(2)}>2 Jogador</div>
        <div className="wstep active">3 Stats</div>
      </div>

      <div className="wizard-context">
        <button className="wizard-back" onClick={() => setStep(2)}>← Trocar jogador</button>
        <div className="wizard-player-info">
          <div className={`wizard-avatar ${posTone(atuacaoForm.posicao_jogo)}`}>{initials(atuacaoForm.jogador)}</div>
          <div>
            <strong>{atuacaoForm.jogador || "—"}</strong>
            <div style={{ fontSize: "0.8rem", color: "var(--text2)" }}>
              {partidaSelecionada ? `vs ${partidaSelecionada.adversario}` : atuacaoForm.partida_id}
            </div>
          </div>
        </div>
        {atuacaoEmEdicaoId && (
          <span className="card-badge" style={{ marginLeft: "auto" }}>edicao</span>
        )}
      </div>

      <form onSubmit={handleSubmit}>
        {/* Minutes control */}
        <div className="minutes-control">
          <span className="minutes-label">Minutos jogados</span>
          <div className="minutes-row">
            <button type="button" className="minutes-btn" onClick={() => setAtuacaoForm((c) => ({ ...c, minutos_jogados: Math.max(0, c.minutos_jogados - 5) }))}>−5</button>
            <button type="button" className="minutes-btn" onClick={() => setAtuacaoForm((c) => ({ ...c, minutos_jogados: Math.max(0, c.minutos_jogados - 1) }))}>−1</button>
            <span className="minutes-val">{atuacaoForm.minutos_jogados}</span>
            <button type="button" className="minutes-btn" onClick={() => setAtuacaoForm((c) => ({ ...c, minutos_jogados: Math.min(120, c.minutos_jogados + 1) }))}>+1</button>
            <button type="button" className="minutes-btn" onClick={() => setAtuacaoForm((c) => ({ ...c, minutos_jogados: Math.min(120, c.minutos_jogados + 5) }))}>+5</button>
          </div>
        </div>

        {/* Quick events — big tap targets */}
        <div className="events-grid-lg">
          {eventosRapidosVisiveis.map((ev) => (
            <button key={ev.value} type="button" className={`event-btn-lg ${ev.tone}`} onClick={() => incrementarEvento(ev.value)}>
              <span className="event-btn-count">{atuacaoForm[ev.value] || 0}</span>
              <span className="event-btn-label">{ev.label}</span>
              <span className="event-btn-hint">{ev.hint}</span>
            </button>
          ))}
        </div>

        {/* Expandable detailed stats */}
        <button type="button" className="detalhes-toggle" onClick={() => setShowDetalhes((v) => !v)}>
          {showDetalhes ? "▲ Ocultar estatísticas detalhadas" : "▼ Mais estatísticas"}
        </button>
        {showDetalhes && (
          <div className="field-grid compact" style={{ marginTop: "12px" }}>
            <label className="field">
              <span>Posicao no jogo</span>
              <select value={atuacaoForm.posicao_jogo} onChange={(e) => setAtuacaoForm((c) => ({ ...c, posicao_jogo: e.target.value }))}>
                {posicoesDisponiveis.map((p) => <option key={p} value={p}>{p}</option>)}
              </select>
            </label>
            {camposAtuacaoVisiveis.map((field) => (
              <label key={field} className="field">
                <span>{formatLabel(field)}</span>
                <input type="number" min="0" value={atuacaoForm[field]} onChange={(e) => setAtuacaoForm((c) => ({ ...c, [field]: Number(e.target.value) }))} />
              </label>
            ))}
          </div>
        )}

        <div className="wizard-actions">
          <button className="btn-save-lg" type="submit">
            {atuacaoEmEdicaoId ? "Atualizar atuacao" : "Salvar atuacao"}
          </button>
          {atuacaoEmEdicaoId && (
            <button className="btn-secondary" type="button" onClick={handleCancelarEdicao}>Cancelar edicao</button>
          )}
        </div>
      </form>
    </div>
  );
}

export default function EntradaDados({
  entryTab, setEntryTab,
  jogadores, partidas, atuacoes,
  posicoesDisponiveis, resultadosDisponiveis, formacoesDisponiveis, funcoesDisponiveis, pesDisponiveis,
  jogadorForm, setJogadorForm, submitJogador,
  partidaForm, setPartidaForm, submitPartida,
  atuacaoForm, setAtuacaoForm, atuacaoEmEdicaoId,
  submitAtuacao, cancelarEdicaoAtuacao,
  selecionarJogadorAtuacao, incrementarEvento, excluirAtuacaoLinha, iniciarEdicaoAtuacao,
  jogadoresOptions, camposAtuacaoVisiveis, eventosRapidosVisiveis,
}) {
  return (
    <div className="page-content">
      <section className="card">
        <div className="card-header"><span className="card-title">Entrada de dados</span></div>
        <div className="card-body">
          <div className="entry-tabs">
            {ENTRY_TABS.map((tab) => (
              <button key={tab.id} className={tab.id === entryTab ? "entry-tab active" : "entry-tab"} onClick={() => setEntryTab(tab.id)}>
                {tab.label}
              </button>
            ))}
          </div>
        </div>
      </section>

      {entryTab === "jogadores" && (
        <div className="entrada-grid">
          <article className="card">
            <div className="card-header"><span className="card-title">Cadastro do elenco</span></div>
            <div className="form-body">
              <form className="field-grid" onSubmit={submitJogador}>
                <label className="field span-2">
                  <span>Nome completo</span>
                  <input value={jogadorForm.jogador} onChange={(e) => setJogadorForm({ ...jogadorForm, jogador: e.target.value })} placeholder="Ex: Joao da Silva" />
                </label>
                <label className="field">
                  <span>Posicao</span>
                  <select value={jogadorForm.posicao} onChange={(e) => setJogadorForm({ ...jogadorForm, posicao: e.target.value })}>
                    {posicoesDisponiveis.map((p) => <option key={p} value={p}>{p}</option>)}
                  </select>
                </label>
                <label className="field">
                  <span>Status</span>
                  <select value={jogadorForm.status} onChange={(e) => setJogadorForm({ ...jogadorForm, status: e.target.value })}>
                    <option value="Ativo">Ativo</option>
                    <option value="Inativo">Inativo</option>
                  </select>
                </label>
                <label className="field">
                  <span>Funcao tatica</span>
                  <select value={jogadorForm.funcao} onChange={(e) => setJogadorForm({ ...jogadorForm, funcao: e.target.value })}>
                    {funcoesDisponiveis.map((f) => <option key={f} value={f}>{f}</option>)}
                  </select>
                </label>
                <label className="field">
                  <span>Pe dominante</span>
                  <select value={jogadorForm.pe_dominante} onChange={(e) => setJogadorForm({ ...jogadorForm, pe_dominante: e.target.value })}>
                    {pesDisponiveis.map((p) => <option key={p} value={p}>{p}</option>)}
                  </select>
                </label>
                <label className="field span-2">
                  <span>Observacoes</span>
                  <textarea value={jogadorForm.observacoes} onChange={(e) => setJogadorForm({ ...jogadorForm, observacoes: e.target.value })} placeholder="Caracteristicas tecnicas, contexto fisico ou comportamento." />
                </label>
                <button className="btn-primary span-2" type="submit">Salvar jogador</button>
              </form>
            </div>
          </article>
          <article className="card">
            <div className="card-header">
              <span className="card-title">Elenco cadastrado</span>
              <span className="card-badge">{jogadores.length}</span>
            </div>
            <div className="card-body">
              <DataTable rows={jogadores.slice().reverse()} preferredOrder={["jogador", "posicao", "status", "funcao", "pe_dominante"]} />
            </div>
          </article>
        </div>
      )}

      {entryTab === "partidas" && (
        <div className="entrada-grid">
          <article className="card">
            <div className="card-header"><span className="card-title">Cadastro de partidas</span></div>
            <div className="form-body">
              <form className="field-grid" onSubmit={submitPartida}>
                <label className="field">
                  <span>Data</span>
                  <input type="date" value={partidaForm.data_partida} onChange={(e) => setPartidaForm({ ...partidaForm, data_partida: e.target.value })} />
                </label>
                <label className="field">
                  <span>Adversario</span>
                  <input value={partidaForm.adversario} onChange={(e) => setPartidaForm({ ...partidaForm, adversario: e.target.value })} />
                </label>
                <label className="field">
                  <span>Competicao</span>
                  <input value={partidaForm.competicao} onChange={(e) => setPartidaForm({ ...partidaForm, competicao: e.target.value })} />
                </label>
                <label className="field">
                  <span>Mando</span>
                  <select value={partidaForm.mandante} onChange={(e) => setPartidaForm({ ...partidaForm, mandante: e.target.value })}>
                    <option value="Casa">Casa</option>
                    <option value="Fora">Fora</option>
                  </select>
                </label>
                <label className="field">
                  <span>Resultado</span>
                  <select value={partidaForm.resultado} onChange={(e) => setPartidaForm({ ...partidaForm, resultado: e.target.value })}>
                    {resultadosDisponiveis.map((r) => <option key={r} value={r}>{r}</option>)}
                  </select>
                </label>
                <label className="field">
                  <span>Formacao</span>
                  <select value={partidaForm.formacao} onChange={(e) => setPartidaForm({ ...partidaForm, formacao: e.target.value })}>
                    {formacoesDisponiveis.map((f) => <option key={f} value={f}>{f}</option>)}
                  </select>
                </label>
                <label className="field span-2">
                  <span>Observacoes</span>
                  <textarea value={partidaForm.observacoes} onChange={(e) => setPartidaForm({ ...partidaForm, observacoes: e.target.value })} placeholder="Contexto da rodada, adversario e observacoes taticas." />
                </label>
                <button className="btn-primary span-2" type="submit">Salvar partida</button>
              </form>
            </div>
          </article>
          <article className="card">
            <div className="card-header">
              <span className="card-title">Partidas cadastradas</span>
              <span className="card-badge">{partidas.length}</span>
            </div>
            <div className="card-body">
              <DataTable rows={partidas.slice().reverse()} preferredOrder={["partida_id", "data_partida", "adversario", "competicao", "resultado", "formacao"]} />
            </div>
          </article>
        </div>
      )}

      {entryTab === "atuacoes" && (
        <div className="entrada-grid wide">
          <article className="card">
            <div className="card-header">
              <span className="card-title">Lancamento de atuacao</span>
              <span className="card-badge">{atuacoes.length} lancadas</span>
            </div>
            <div className="form-body">
              <AtuacaoWizard
                partidas={partidas}
                jogadores={jogadores}
                atuacoes={atuacoes}
                atuacaoForm={atuacaoForm}
                setAtuacaoForm={setAtuacaoForm}
                atuacaoEmEdicaoId={atuacaoEmEdicaoId}
                submitAtuacao={submitAtuacao}
                cancelarEdicaoAtuacao={cancelarEdicaoAtuacao}
                selecionarJogadorAtuacao={selecionarJogadorAtuacao}
                incrementarEvento={incrementarEvento}
                excluirAtuacaoLinha={excluirAtuacaoLinha}
                iniciarEdicaoAtuacao={iniciarEdicaoAtuacao}
                jogadoresOptions={jogadoresOptions}
                camposAtuacaoVisiveis={camposAtuacaoVisiveis}
                eventosRapidosVisiveis={eventosRapidosVisiveis}
                posicoesDisponiveis={posicoesDisponiveis}
              />
            </div>
          </article>

          <article className="card">
            <div className="card-header">
              <span className="card-title">Atuacoes registradas</span>
              <span className="card-badge">{atuacoes.length}</span>
            </div>
            <div className="card-body">
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr><th>partida_id</th><th>jogador</th><th>pos</th><th>min</th><th>g</th><th>a</th><th></th></tr>
                  </thead>
                  <tbody>
                    {atuacoes.length
                      ? atuacoes.slice().reverse().map((a) => (
                        <tr key={a.atuacao_id}>
                          <td>{a.partida_id}</td>
                          <td>{a.jogador}</td>
                          <td>{a.posicao_jogo}</td>
                          <td>{a.minutos_jogados}</td>
                          <td>{a.gols}</td>
                          <td>{a.assistencias}</td>
                          <td>
                            <div className="row-actions">
                              <button className="table-button" type="button" onClick={() => iniciarEdicaoAtuacao(a)}>Editar</button>
                              <button className="table-button danger" type="button" onClick={() => excluirAtuacaoLinha(a.atuacao_id)}>Excluir</button>
                            </div>
                          </td>
                        </tr>
                      ))
                      : <tr><td colSpan="7"><div className="empty-state">Nenhuma atuacao disponivel.</div></td></tr>
                    }
                  </tbody>
                </table>
              </div>
            </div>
          </article>
        </div>
      )}
    </div>
  );
}
