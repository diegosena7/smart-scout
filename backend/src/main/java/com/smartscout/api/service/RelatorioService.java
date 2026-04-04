package com.smartscout.api.service;

import com.smartscout.api.config.ContextoConst;
import com.smartscout.api.entity.Atuacao;
import com.smartscout.api.entity.Jogador;
import com.smartscout.api.entity.Partida;
import com.smartscout.api.repository.AtuacaoRepository;
import com.smartscout.api.repository.JogadorRepository;
import com.smartscout.api.repository.PartidaRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private final AnaliseElencoService analiseService;
    private final JogadorRepository jogadorRepo;
    private final PartidaRepository partidaRepo;
    private final AtuacaoRepository atuacaoRepo;

    public RelatorioService(AnaliseElencoService analiseService,
                            JogadorRepository jogadorRepo,
                            PartidaRepository partidaRepo,
                            AtuacaoRepository atuacaoRepo) {
        this.analiseService = analiseService;
        this.jogadorRepo = jogadorRepo;
        this.partidaRepo = partidaRepo;
        this.atuacaoRepo = atuacaoRepo;
    }

    public Map<String, Object> contexto() {
        long qtdJogadores = jogadorRepo.count();
        long qtdPartidas = partidaRepo.count();
        long qtdAtuacoes = atuacaoRepo.count();
        String modo = qtdAtuacoes > 0 ? "operacional" : "cadastro";

        Map<String, Object> ctx = new LinkedHashMap<>();
        ctx.put("modo_analitico", modo);
        ctx.put("quantidade_jogadores", qtdJogadores);
        ctx.put("quantidade_partidas", qtdPartidas);
        ctx.put("quantidade_atuacoes", qtdAtuacoes);
        ctx.put("posicoes_validas", ContextoConst.POSICOES_VALIDAS);
        ctx.put("resultados_validos", ContextoConst.RESULTADOS_VALIDOS);
        ctx.put("funcoes_taticas_validas", ContextoConst.FUNCOES_TATICAS_VALIDAS);
        ctx.put("pes_dominantes_validos", ContextoConst.PES_DOMINANTES_VALIDOS);
        ctx.put("formacoes_disponiveis", ContextoConst.FORMACOES_DISPONIVEIS);
        return ctx;
    }

    public Map<String, Object> dashboard() {
        List<Map<String, Object>> elenco = analiseService.construirAnalise();
        List<Atuacao> atuacoes = atuacaoRepo.findAll();
        List<Jogador> jogadores = jogadorRepo.findAll();
        List<Partida> partidas = partidaRepo.findAll();

        long ativos = jogadores.stream().filter(j -> "Ativo".equals(j.getStatus())).count();

        // Titulares: top 1 por posição (melhor score_titularidade)
        Map<String, Map<String, Object>> titularesPorPos = new LinkedHashMap<>();
        for (var m : elenco) {
            Object posObj = m.get("posicao");
            String pos = posObj instanceof String ? (String) posObj : "";
            if (!titularesPorPos.containsKey(pos)) {
                titularesPorPos.put(pos, m);
            }
        }
        List<Map<String, Object>> titulares = titularesPorPos.values().stream()
                .map(m -> pick(m, "jogador", "posicao", "score_titularidade", "score_momento", "rank_titularidade_posicao", "tendencia_recente"))
                .toList();

        // Destaques de momento: top 3 por score_momento
        List<Map<String, Object>> destaques = elenco.stream()
                .sorted(Comparator.comparingDouble(m -> -toDouble(m.get("score_momento"))))
                .limit(3)
                .map(m -> pick(m, "jogador", "posicao", "score_momento", "tendencia_recente"))
                .toList();

        // Disputas: top 2 por posição
        Map<String, List<Map<String, Object>>> disputasPorPos = new LinkedHashMap<>();
        for (var m : elenco) {
            Object posObj = m.get("posicao");
            String pos = posObj instanceof String ? (String) posObj : "";
            disputasPorPos.computeIfAbsent(pos, k -> new ArrayList<>()).add(m);
        }
        List<Map<String, Object>> disputas = disputasPorPos.values().stream()
                .flatMap(lista -> lista.stream()
                        .sorted(Comparator.comparingDouble(m -> -toDouble(m.get("score_titularidade"))))
                        .limit(2))
                .map(m -> pick(m, "jogador", "posicao", "score_titularidade", "rank_titularidade_posicao"))
                .toList();

        // Últimas 5 atuações
        List<Map<String, Object>> ultimasAtuacoes = atuacoes.stream()
                .sorted(Comparator.comparing(a -> a.getAtuacaoId()))
                .skip(Math.max(0, atuacoes.size() - 5))
                .sorted(Comparator.comparing(a -> a.getAtuacaoId(), Comparator.reverseOrder()))
                .map(a -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("atuacao_id", a.getAtuacaoId());
                    m.put("partida_id", a.getPartidaId());
                    m.put("jogador", a.getJogador());
                    m.put("posicao_jogo", a.getPosicaoJogo());
                    m.put("minutos_jogados", a.getMinutosJogados());
                    m.put("gols", a.getGols());
                    m.put("assistencias", a.getAssistencias());
                    return m;
                })
                .toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("cards", Map.of(
                "jogadores_ativos", ativos,
                "partidas_registradas", partidas.size(),
                "atuacoes_lancadas", atuacoes.size(),
                "modo_analitico", atuacoes.isEmpty() ? "cadastro" : "operacional"
        ));
        result.put("titulares_recomendados", titulares);
        result.put("destaques_momento", destaques);
        result.put("disputas_posicao", disputas);
        result.put("ultimas_atuacoes", ultimasAtuacoes);
        return result;
    }

    public List<Map<String, Object>> analiseElenco(String posicao) {
        return analiseService.construirAnalise(posicao);
    }

    public List<Map<String, Object>> buscarJogador(String identificador) {
        return analiseService.construirAnalise().stream()
                .filter(m -> {
                    Object idObj = m.get("jogador_id");
                    Object nomeObj = m.get("jogador");
                    String id = idObj instanceof String ? (String) idObj : "";
                    String nome = nomeObj instanceof String ? (String) nomeObj : "";
                    return id.equals(identificador)
                            || nome.toLowerCase().contains(identificador.toLowerCase());
                })
                .toList();
    }

    public String explicar(String identificador) {
        return analiseService.findPlayerCalc(identificador)
                .map(p -> String.format(
                        "%s atua como %s e ocupa a posicao %d no ranking da posicao. " +
                        "Score de titularidade: %.2f (desempenho %.2f, disciplina %.2f). " +
                        "Momento atual: %.2f. Tendencia: %s.",
                        p.getJogador(), p.getPosicao(), p.getRankTitularidadePosicao(),
                        p.getScoreTitularidade(), p.getScoreDesempenho(), p.getScoreDisciplina(),
                        p.getScoreMomento(), p.getTendenciaRecente()
                ))
                .orElse("Jogador nao encontrado.");
    }

    public List<Map<String, Object>> comparar(String idA, String idB) {
        List<Map<String, Object>> analise = analiseService.construirAnalise();
        List<Map<String, Object>> result = new ArrayList<>();
        analise.stream()
                .filter(m -> {
                    Object idObj = m.get("jogador_id");
                    Object nomeObj = m.get("jogador");
                    String id = idObj instanceof String ? (String) idObj : "";
                    String nome = nomeObj instanceof String ? (String) nomeObj : "";
                    return id.equals(idA)
                            || nome.toLowerCase().contains(idA.toLowerCase());
                })
                .findFirst()
                .ifPresent(result::add);
        analise.stream()
                .filter(m -> {
                    Object idObj = m.get("jogador_id");
                    Object nomeObj = m.get("jogador");
                    String id = idObj instanceof String ? (String) idObj : "";
                    String nome = nomeObj instanceof String ? (String) nomeObj : "";
                    Object firstId = result.isEmpty() ? null : result.get(0).get("jogador_id");
                    String firstIdStr = firstId instanceof String ? (String) firstId : "";
                    return (id.equals(idB) || nome.toLowerCase().contains(idB.toLowerCase()))
                            && (result.isEmpty() || !id.equals(firstIdStr));
                })
                .findFirst()
                .ifPresent(result::add);
        return result;
    }

    public List<Map<String, Object>> timeIdeal(String formacao) {
        Map<String, Integer> slots = ContextoConst.FORMACOES_BASE.get(formacao);
        if (slots == null) throw new IllegalArgumentException("Formacao desconhecida: " + formacao);

        List<Map<String, Object>> elenco = analiseService.construirAnalise();
        List<Map<String, Object>> escalacao = new ArrayList<>();
        Set<String> usados = new HashSet<>();

        for (var entry : slots.entrySet()) {
            String posicao = entry.getKey();
            int vagas = entry.getValue();
            elenco.stream()
                    .filter(m -> {
                        Object posObj = m.get("posicao");
                        Object idObj = m.get("jogador_id");
                        String pos = posObj instanceof String ? (String) posObj : "";
                        String id = idObj instanceof String ? (String) idObj : "";
                        return posicao.equals(pos) && !usados.contains(id);
                    })
                    .sorted(Comparator.comparingDouble(m -> -toDouble(m.get("score_titularidade"))))
                    .limit(vagas)
                    .forEach(m -> {
                        Object idObj = m.get("jogador_id");
                        String id = idObj instanceof String ? (String) idObj : "";
                        usados.add(id);
                        Map<String, Object> slot = new LinkedHashMap<>(m);
                        slot.put("vaga_formacao", posicao);
                        escalacao.add(slot);
                    });
        }
        return escalacao;
    }

    // ---------- helpers ----------

    private static Map<String, Object> pick(Map<String, Object> source, String... keys) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (String k : keys) result.put(k, source.get(k));
        return result;
    }

    private static double toDouble(Object val) {
        if (val instanceof Number n) return n.doubleValue();
        return 0;
    }
}
