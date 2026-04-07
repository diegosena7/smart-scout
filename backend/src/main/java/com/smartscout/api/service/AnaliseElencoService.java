package com.smartscout.api.service;

import com.smartscout.api.config.ContextoConst;
import com.smartscout.api.entity.Atuacao;
import com.smartscout.api.entity.Jogador;
import com.smartscout.api.entity.Partida;
import com.smartscout.api.repository.AtuacaoRepository;
import com.smartscout.api.repository.JogadorRepository;
import com.smartscout.api.repository.PartidaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Service
public class AnaliseElencoService {

    private static final Logger logger = LoggerFactory.getLogger(AnaliseElencoService.class);
    private final JogadorRepository jogadorRepo;
    private final PartidaRepository partidaRepo;
    private final AtuacaoRepository atuacaoRepo;

    public AnaliseElencoService(JogadorRepository jogadorRepo,
                                PartidaRepository partidaRepo,
                                AtuacaoRepository atuacaoRepo) {
        this.jogadorRepo = jogadorRepo;
        this.partidaRepo = partidaRepo;
        this.atuacaoRepo = atuacaoRepo;
    }

    public List<Map<String, Object>> construirAnalise() {
        return construirAnalise(null);
    }

    public List<Map<String, Object>> construirAnalise(String posicaoFiltro) {
        logger.info("GET /analise/elenco - Iniciando análise do elenco. Filtro: {}", posicaoFiltro);
        try {
            List<Jogador> jogadores = jogadorRepo.findAll();
            List<Partida> partidas = partidaRepo.findAllByOrderByDataPartidaAsc();
            List<Atuacao> atuacoes = atuacaoRepo.findAll();
            int totalPartidas = Math.max(1, partidas.size());

            logger.info("GET /analise/elenco - Dados carregados: {} jogadores, {} partidas, {} atuações",
                jogadores.size(), partidas.size(), atuacoes.size());

            if (jogadores.isEmpty() || atuacoes.isEmpty()) {
                logger.warn("GET /analise/elenco - Elenco vazio ou sem atuações");
                return List.of();
            }

        Map<String, LocalDate> datasPorPartida = partidas.stream()
                .collect(Collectors.toMap(Partida::getPartidaId, Partida::getDataPartida));

        Map<String, Jogador> jogadoresMap = jogadores.stream()
                .collect(Collectors.toMap(Jogador::getJogadorId, j -> j));

        Map<String, List<Atuacao>> atuacoesPorJogador = atuacoes.stream()
                .collect(Collectors.groupingBy(Atuacao::getJogadorId));

        List<PlayerCalc> players = new ArrayList<>();

        for (var entry : atuacoesPorJogador.entrySet()) {
            String jogadorId = entry.getKey();
            List<Atuacao> ats = entry.getValue();
            Jogador jogador = jogadoresMap.get(jogadorId);
            if (jogador == null) continue;

            PlayerCalc p = new PlayerCalc();
            p.setJogadorId(jogadorId);
            p.setJogador(jogador.getJogador());
            p.setPosicao(jogador.getPosicao());
            p.setJogos(ats.size());

            // Aggregate raw stats
            p.setMinutosJogados(sum(ats, Atuacao::getMinutosJogados));
            p.setGols(sum(ats, Atuacao::getGols));
            p.setAssistencias(sum(ats, Atuacao::getAssistencias));
            p.setFinalizacoes(sum(ats, Atuacao::getFinalizacoes));
            p.setFinalizacoesNoAlvo(sum(ats, Atuacao::getFinalizacoesNoAlvo));
            p.setPassesDecisivos(sum(ats, Atuacao::getPassesDecisivos));
            p.setPassesCertos(sum(ats, Atuacao::getPassesCertos));
            p.setDesarmes(sum(ats, Atuacao::getDesarmes));
            p.setDuelosGanhos(sum(ats, Atuacao::getDuelosGanhos));
            p.setInterceptacoes(sum(ats, Atuacao::getInterceptacoes));
            p.setRecuperacoesBola(sum(ats, Atuacao::getRecuperacoesBola));
            p.setCruzamentosCertos(sum(ats, Atuacao::getCruzamentosCertos));
            p.setDefesasDificeis(sum(ats, Atuacao::getDefesasDificeis));
            p.setGolsSofridos(sum(ats, Atuacao::getGolsSofridos));
            p.setJogosSemSofrerGols(sum(ats, Atuacao::getJogosSemSofrerGols));
            p.setCartoesAmarelos(sum(ats, Atuacao::getCartoesAmarelos));
            p.setCartoesVermelhos(sum(ats, Atuacao::getCartoesVermelhos));
            p.setFaltasCometidas(sum(ats, Atuacao::getFaltasCometidas));

            // Minutos últimos 5 jogos
            int min5 = ats.stream()
                    .sorted(Comparator.comparing(a -> datasPorPartida.getOrDefault(a.getPartidaId(), LocalDate.MIN)))
                    .skip(Math.max(0, ats.size() - 5))
                    .mapToInt(Atuacao::getMinutosJogados)
                    .sum();
            p.setMinutosUltimos5Jogos(min5);

             // Média de nota do técnico (se houver)
             List<Integer> notasTecnico = ats.stream()
                     .map(Atuacao::getNotaTecnico)
                     .filter(n -> n != null)
                     .toList();
             double mediaNotaTecnico = notasTecnico.isEmpty() ? 0 :
                     notasTecnico.stream().mapToInt(Integer::intValue).average().orElse(0);
             p.setScoreNotaTecnico(mediaNotaTecnico);  // Armazenar média bruta (1-5) por enquanto

             computeMetricasPor90(p);
             players.add(p);
         }

         computeDesempenhoBruto(players, atuacoesPorJogador);
         normalizeByPosition(players, PlayerCalc::getDesempenhoBruto, PlayerCalc::setScoreDesempenho);
         computeDisciplina(players);
         computeConfiabilidade(players, totalPartidas);
        computeTitularidade(players);
        computeUsoRecente(players);
        normalizeByPosition(players, PlayerCalc::getIndicUsoRecente, PlayerCalc::setScoreUsoRecente);
        computeMomento(players);
        computeRankings(players);

        List<PlayerCalc> filtrados = posicaoFiltro == null || posicaoFiltro.isBlank()
                ? players
                : players.stream().filter(p -> p.getPosicao().equals(posicaoFiltro)).toList();

        var resultado = filtrados.stream()
                .sorted(Comparator.comparing(PlayerCalc::getPosicao)
                        .thenComparingInt(PlayerCalc::getRankTitularidadePosicao))
                .map(this::toMap)
                .toList();
        
        logger.info("GET /analise/elenco - Análise concluída. Retornando {} jogadores", resultado.size());
        return resultado;
        } catch (Exception e) {
            logger.error("GET /analise/elenco - Erro ao construir análise: ", e);
            throw e;
        }
    }

    // ---------- métricas por 90 ----------

    private void computeMetricasPor90(PlayerCalc p) {
        double min = p.getMinutosJogados();
        double jogos = Math.max(1, p.getJogos());

        p.setGolsPor90(por90(p.getGols(), min));
        p.setAssistenciasPor90(por90(p.getAssistencias(), min));
        p.setFinalizacoesNoAlvoPor90(por90(p.getFinalizacoesNoAlvo(), min));
        p.setPassesDecisosPor90(por90(p.getPassesDecisivos(), min));
        p.setPassesCertosPor90(por90(p.getPassesCertos(), min));
        p.setDesarmesPor90(por90(p.getDesarmes(), min));
        p.setDuelosGanhosPor90(por90(p.getDuelosGanhos(), min));
        p.setInterceptacoesPor90(por90(p.getInterceptacoes(), min));
        p.setRecuperacoesBolaPor90(por90(p.getRecuperacoesBola(), min));
        p.setCruzamentosCertosPor90(por90(p.getCruzamentosCertos(), min));
        p.setDefesasDificeisPor90(por90(p.getDefesasDificeis(), min));
        p.setGolsSofridosPor90(por90(p.getGolsSofridos(), min));

        double taxa = p.getFinalizacoes() > 0
                ? (double) p.getFinalizacoesNoAlvo() / p.getFinalizacoes() : 0;
        p.setTaxaFinalizacoesNoAlvo(taxa);
        p.setTaxaFinalizacoesNoAlvoPonderada(taxa * 20);

        p.setGolsSofridosPor90Invertido(1.0 / (1.0 + p.getGolsSofridosPor90()));
        p.setJogosSemSofrerGolsPorJogo(p.getJogosSemSofrerGols() / jogos);
        p.setJogosSemSofrerGolsPorJogoPonderado(p.getJogosSemSofrerGolsPorJogo() * 100);
    }

     // ---------- desempenho bruto por posição ----------

     private void computeDesempenhoBruto(List<PlayerCalc> players, Map<String, List<Atuacao>> atuacoesPorJogador) {
         for (PlayerCalc p : players) {
             double desempenho = switch (p.getPosicao()) {
                 case "Atacante" ->
                         p.getGolsPor90() * 0.35
                         + p.getAssistenciasPor90() * 0.20
                         + p.getFinalizacoesNoAlvoPor90() * 0.20
                         + p.getTaxaFinalizacoesNoAlvoPonderada() * 0.10
                         + p.getPassesDecisosPor90() * 0.15;
                 case "Meia" ->
                         p.getAssistenciasPor90() * 0.25
                         + p.getPassesDecisosPor90() * 0.35
                         + p.getPassesCertosPor90() * 0.20
                         + p.getGolsPor90() * 0.20;
                 case "Zagueiro" ->
                         p.getGolsPor90() * 0.10
                         + p.getAssistenciasPor90() * 0.05
                         + p.getDesarmesPor90() * 0.25
                         + p.getInterceptacoesPor90() * 0.20
                         + p.getDuelosGanhosPor90() * 0.25
                         + p.getPassesCertosPor90() * 0.15;
                 case "Lateral" ->
                         p.getGolsPor90() * 0.10
                         + p.getAssistenciasPor90() * 0.15
                         + p.getDesarmesPor90() * 0.15
                         + p.getInterceptacoesPor90() * 0.15
                         + p.getCruzamentosCertosPor90() * 0.20
                         + p.getPassesDecisosPor90() * 0.20
                         + p.getRecuperacoesBolaPor90() * 0.05;
                 case "Volante" ->
                         p.getGolsPor90() * 0.05
                         + p.getAssistenciasPor90() * 0.05
                         + p.getDesarmesPor90() * 0.25
                         + p.getInterceptacoesPor90() * 0.25
                         + p.getRecuperacoesBolaPor90() * 0.20
                         + p.getPassesCertosPor90() * 0.20;
                 case "Goleiro" ->
                         p.getDefesasDificeisPor90() * 0.45
                         + p.getGolsSofridosPor90Invertido() * 0.30
                         + p.getJogosSemSofrerGolsPorJogoPonderado() * 0.25;
                 default ->
                         p.getGolsPor90() * 0.25
                         + p.getPassesDecisosPor90() * 0.20
                         + p.getDesarmesPor90() * 0.20
                         + p.getDuelosGanhosPor90() * 0.20
                         + p.getPassesCertosPor90() * 0.15;
             };

             // Adicionar contribuição da nota do técnico normalizada (1-5 -> 0-100)
             double scoreNotaNormalizado = p.getScoreNotaTecnico() > 0 ?
                     (p.getScoreNotaTecnico() / 5.0) * 100 : 50;
             desempenho = desempenho * (1 - ContextoConst.PESO_DESEMPENHO_NOTA_TECNICO)
                     + scoreNotaNormalizado * ContextoConst.PESO_DESEMPENHO_NOTA_TECNICO;

             p.setDesempenhoBruto(desempenho);
         }
     }

    // ---------- disciplina ----------

    private void computeDisciplina(List<PlayerCalc> players) {
        // Normalize by position to ensure fairness within each role
        // A defender is not compared to a striker for discipline — they play differently
        Map<String, List<PlayerCalc>> byPos = players.stream()
                .collect(Collectors.groupingBy(PlayerCalc::getPosicao));

        for (List<PlayerCalc> grupo : byPos.values()) {
            double min = grupo.stream().mapToDouble(this::disciplinaBruta).min().orElse(0);
            double max = grupo.stream().mapToDouble(this::disciplinaBruta).max().orElse(0);
            for (PlayerCalc p : grupo) {
                double normalizado = normalize(disciplinaBruta(p), min, max);
                p.setScoreDisciplina(round2(100 - normalizado));
            }
        }
    }

    private double disciplinaBruta(PlayerCalc p) {
        return p.getCartoesAmarelos() * ContextoConst.PESO_DISCIPLINA_AMARELO
                + p.getCartoesVermelhos() * ContextoConst.PESO_DISCIPLINA_VERMELHO
                + p.getFaltasCometidas() * ContextoConst.PESO_DISCIPLINA_FALTA;
    }

    // ---------- confiabilidade / presença ----------

    private void computeConfiabilidade(List<PlayerCalc> players, int totalPartidas) {
        for (PlayerCalc p : players) {
            double taxa = (double) p.getJogos() / totalPartidas;
            p.setTaxaPresenca(round4(Math.min(1.0, taxa)));
            p.setScoreConfiabilidade(round2(p.getTaxaPresenca() * 100));
        }
    }

    // ---------- titularidade ----------

    private void computeTitularidade(List<PlayerCalc> players) {
        for (PlayerCalc p : players) {
            p.setScoreTitularidade(round2(
                    p.getScoreDesempenho() * ContextoConst.PESO_TITULARIDADE_DESEMPENHO
                    + p.getScoreDisciplina() * ContextoConst.PESO_TITULARIDADE_DISCIPLINA
                    + p.getScoreConfiabilidade() * ContextoConst.PESO_TITULARIDADE_CONFIABILIDADE
            ));
        }
    }

    // ---------- uso recente ----------

    private void computeUsoRecente(List<PlayerCalc> players) {
        for (PlayerCalc p : players) {
            double indice;

            if (p.getJogos() < 5) {
                // For players with < 5 games, use direct average of recent minutes
                // compared to a "full match" baseline (90 minutes)
                // This avoids penalizing new players unfairly
                double mediaMinutosRecentes = p.getMinutosUltimos5Jogos() / Math.max(1, p.getJogos());
                indice = mediaMinutosRecentes / 90.0;
            } else {
                // For players with 5+ games, use the expectation-based formula
                double mediaMinutos = (double) p.getMinutosJogados() / p.getJogos();
                double expectativa = mediaMinutos * 5;
                indice = expectativa > 0 ? p.getMinutosUltimos5Jogos() / expectativa : 0;
            }

            p.setIndicUsoRecente(round4(Math.min(1.0, indice))); // Cap at 1.0 for normalization
        }
    }

    // ---------- momento, delta e tendência ----------

    private void computeMomento(List<PlayerCalc> players) {
        for (PlayerCalc p : players) {
            p.setScoreMomento(round2(
                    p.getScoreTitularidade() * ContextoConst.PESO_MOMENTO_TITULARIDADE
                    + p.getScoreUsoRecente() * ContextoConst.PESO_MOMENTO_USO_RECENTE
            ));
            p.setDeltaMomento(round2(p.getScoreMomento() - p.getScoreTitularidade()));
            p.setTendenciaRecente(
                    p.getDeltaMomento() >= ContextoConst.DELTA_GANHOU_ESPACO ? "ganhou espaco"
                    : p.getDeltaMomento() <= ContextoConst.DELTA_PERDEU_ESPACO ? "perdeu espaco"
                    : "estavel"
            );
        }
    }

    // ---------- rankings por posição ----------

    private void computeRankings(List<PlayerCalc> players) {
        Map<String, List<PlayerCalc>> byPos = players.stream()
                .collect(Collectors.groupingBy(PlayerCalc::getPosicao));

        for (List<PlayerCalc> grupo : byPos.values()) {
            List<PlayerCalc> porTitularidade = grupo.stream()
                    .sorted(Comparator.comparingDouble(PlayerCalc::getScoreTitularidade).reversed())
                    .toList();
            for (int i = 0; i < porTitularidade.size(); i++) {
                porTitularidade.get(i).setRankTitularidadePosicao(i + 1);
            }
            List<PlayerCalc> porMomento = grupo.stream()
                    .sorted(Comparator.comparingDouble(PlayerCalc::getScoreMomento).reversed())
                    .toList();
            for (int i = 0; i < porMomento.size(); i++) {
                porMomento.get(i).setRankMomentoPosicao(i + 1);
            }
        }
    }

    // ---------- normalização ----------

    private void normalizeByPosition(List<PlayerCalc> players,
                                      ToDoubleFunction<PlayerCalc> getter,
                                      ObjDoubleConsumer<PlayerCalc> setter) {
        Map<String, List<PlayerCalc>> byPos = players.stream()
                .collect(Collectors.groupingBy(PlayerCalc::getPosicao));
        for (List<PlayerCalc> grupo : byPos.values()) {
            double min = grupo.stream().mapToDouble(getter).min().orElse(0);
            double max = grupo.stream().mapToDouble(getter).max().orElse(0);
            for (PlayerCalc p : grupo) {
                setter.accept(p, round2(normalize(getter.applyAsDouble(p), min, max)));
            }
        }
    }

    static double normalize(double value, double min, double max) {
        if (max == min) return 50.0;
        return (value - min) / (max - min) * 100.0;
    }

    // ---------- helpers ----------

    private static double por90(int valor, double minutos) {
        return minutos > 0 ? round4((valor * 90.0) / minutos) : 0;
    }

    private static int sum(List<Atuacao> ats, java.util.function.ToIntFunction<Atuacao> fn) {
        return ats.stream().mapToInt(fn).sum();
    }

    private static double round2(double v) { return Math.round(v * 100.0) / 100.0; }
    private static double round4(double v) { return Math.round(v * 10000.0) / 10000.0; }

    // ---------- conversão para Map (resposta JSON) ----------

    Map<String, Object> toMap(PlayerCalc p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("jogador_id", p.getJogadorId());
        m.put("jogador", p.getJogador());
        m.put("posicao", p.getPosicao());
        m.put("jogos", p.getJogos());
        m.put("minutos_jogados", p.getMinutosJogados());
        m.put("gols", p.getGols());
        m.put("assistencias", p.getAssistencias());
        m.put("score_desempenho", p.getScoreDesempenho());
        m.put("score_disciplina", p.getScoreDisciplina());
        m.put("score_confiabilidade", p.getScoreConfiabilidade());
        m.put("score_titularidade", p.getScoreTitularidade());
        m.put("score_uso_recente", p.getScoreUsoRecente());
        m.put("score_momento", p.getScoreMomento());
        m.put("delta_momento", p.getDeltaMomento());
        m.put("tendencia_recente", p.getTendenciaRecente());
        m.put("rank_titularidade_posicao", p.getRankTitularidadePosicao());
        m.put("rank_momento_posicao", p.getRankMomentoPosicao());
        return m;
    }

    public Optional<PlayerCalc> findPlayerCalc(String identificador) {
        List<Jogador> jogadores = jogadorRepo.findAll();
        List<Atuacao> atuacoes = atuacaoRepo.findAll();
        List<Partida> partidas = partidaRepo.findAllByOrderByDataPartidaAsc();

        if (atuacoes.isEmpty()) return Optional.empty();

        // Resolve jogador_id — aceita ID direto ou busca por nome
        String jogadorId = jogadores.stream()
                .filter(j -> j.getJogadorId().equals(identificador)
                        || j.getJogador().toLowerCase().contains(identificador.toLowerCase()))
                .map(Jogador::getJogadorId)
                .findFirst()
                .orElse(null);
        if (jogadorId == null) return Optional.empty();

        Map<String, LocalDate> datasPorPartida = partidas.stream()
                .collect(Collectors.toMap(Partida::getPartidaId, Partida::getDataPartida));
        Map<String, Jogador> jogadoresMap = jogadores.stream()
                .collect(Collectors.toMap(Jogador::getJogadorId, j -> j));

        List<Atuacao> ats = atuacoes.stream()
                .filter(a -> a.getJogadorId().equals(jogadorId))
                .toList();
        if (ats.isEmpty()) return Optional.empty();

        // Build a single-player list and run the full pipeline for normalization context
        // Use full squad for normalization, then find the player
        List<Map<String, Object>> analise = construirAnalise();
        return analise.stream()
                .filter(m -> jogadorId.equals(m.get("jogador_id")))
                .map(m -> {
                    // Reconstrói PlayerCalc a partir do mapa para reutilizar explicação
                    PlayerCalc pc = new PlayerCalc();
                    pc.setJogadorId((String) m.get("jogador_id"));
                    pc.setJogador((String) m.get("jogador"));
                    pc.setPosicao((String) m.get("posicao"));
                    pc.setJogos((int) m.get("jogos"));
                    pc.setScoreDesempenho((double) m.get("score_desempenho"));
                    pc.setScoreDisciplina((double) m.get("score_disciplina"));
                    pc.setScoreConfiabilidade((double) m.get("score_confiabilidade"));
                    pc.setScoreTitularidade((double) m.get("score_titularidade"));
                    pc.setScoreUsoRecente((double) m.get("score_uso_recente"));
                    pc.setScoreMomento((double) m.get("score_momento"));
                    pc.setDeltaMomento((double) m.get("delta_momento"));
                    pc.setTendenciaRecente((String) m.get("tendencia_recente"));
                    pc.setRankTitularidadePosicao((int) m.get("rank_titularidade_posicao"));
                    pc.setRankMomentoPosicao((int) m.get("rank_momento_posicao"));
                    return pc;
                })
                .findFirst();
    }
}
