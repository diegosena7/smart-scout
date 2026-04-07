package com.smartscout.api.controller;

import com.smartscout.api.service.RelatorioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analise")
public class AnaliseController {

    private static final Logger logger = LoggerFactory.getLogger(AnaliseController.class);
    private final RelatorioService relatorio;

    public AnaliseController(RelatorioService relatorio) {
        this.relatorio = relatorio;
    }

    @GetMapping("/elenco")
    public Map<String, Object> elenco(@RequestParam(required = false) String posicao) {
        logger.info("[REQUEST] GET /analise/elenco - Posição: {}", posicao);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", relatorio.analiseElenco(posicao));
        result.putAll(relatorio.contexto());
        logger.info("[RESPONSE] GET /analise/elenco - Análise concluída");
        return result;
    }

    @GetMapping("/jogador/{identificador}")
    public Map<String, Object> jogador(@PathVariable String identificador) {
        logger.info("[REQUEST] GET /analise/jogador/{}", identificador);
        List<Map<String, Object>> items = relatorio.buscarJogador(identificador);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", items);
        result.put("explicacao", relatorio.explicar(identificador));
        result.putAll(relatorio.contexto());
        logger.info("[RESPONSE] GET /analise/jogador/{} - {} jogadores encontrados", identificador, items.size());
        return result;
    }

    @GetMapping("/comparar")
    public Map<String, Object> comparar(@RequestParam("jogador_a") String jogadorA,
                                         @RequestParam("jogador_b") String jogadorB) {
        logger.info("[REQUEST] GET /analise/comparar - Comparando {} vs {}", jogadorA, jogadorB);
        List<Map<String, Object>> items = relatorio.comparar(jogadorA, jogadorB);
        if (items.isEmpty()) {
            logger.warn("[RESPONSE] GET /analise/comparar - Jogadores não encontrados para comparação");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Jogadores nao encontrados para comparacao.");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", items);
        result.put("explicacoes", Map.of(
                jogadorA, relatorio.explicar(jogadorA),
                jogadorB, relatorio.explicar(jogadorB)
        ));
        result.putAll(relatorio.contexto());
        logger.info("[RESPONSE] GET /analise/comparar - Comparação concluída");
        return result;
    }

    @GetMapping("/time-ideal")
    public Map<String, Object> timeIdeal(@RequestParam(defaultValue = "4-3-3") String formacao,
                                         @RequestParam(required = false) String partidaId) {
        logger.info("[REQUEST] GET /analise/time-ideal - Formação: {}, Partida: {}", formacao, partidaId);
        try {
            Map<String, Object> result = new LinkedHashMap<>(relatorio.timeIdeal(formacao, partidaId));
            result.putAll(relatorio.contexto());
            logger.info("[RESPONSE] GET /analise/time-ideal - Escalação gerada");
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("[RESPONSE] GET /analise/time-ideal - Erro: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/validacao")
    public Map<String, Object> validacao() {
        logger.info("[REQUEST] GET /analise/validacao");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("erros", List.of());
        result.put("avisos", List.of());
        result.putAll(relatorio.contexto());
        logger.info("[RESPONSE] GET /analise/validacao - Validação concluída");
        return result;
    }
}
