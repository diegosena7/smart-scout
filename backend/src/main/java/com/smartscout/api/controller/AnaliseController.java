package com.smartscout.api.controller;

import com.smartscout.api.service.RelatorioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analise")
public class AnaliseController {

    private final RelatorioService relatorio;

    public AnaliseController(RelatorioService relatorio) {
        this.relatorio = relatorio;
    }

    @GetMapping("/elenco")
    public Map<String, Object> elenco(@RequestParam(required = false) String posicao) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", relatorio.analiseElenco(posicao));
        result.putAll(relatorio.contexto());
        return result;
    }

    @GetMapping("/jogador/{identificador}")
    public Map<String, Object> jogador(@PathVariable String identificador) {
        List<Map<String, Object>> items = relatorio.buscarJogador(identificador);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", items);
        result.put("explicacao", relatorio.explicar(identificador));
        result.putAll(relatorio.contexto());
        return result;
    }

    @GetMapping("/comparar")
    public Map<String, Object> comparar(@RequestParam("jogador_a") String jogadorA,
                                         @RequestParam("jogador_b") String jogadorB) {
        List<Map<String, Object>> items = relatorio.comparar(jogadorA, jogadorB);
        if (items.isEmpty()) {
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
        return result;
    }

    @GetMapping("/time-ideal")
    public Map<String, Object> timeIdeal(@RequestParam(defaultValue = "4-3-3") String formacao) {
        try {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("items", relatorio.timeIdeal(formacao));
            result.putAll(relatorio.contexto());
            return result;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/validacao")
    public Map<String, Object> validacao() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("erros", List.of());
        result.put("avisos", List.of());
        result.putAll(relatorio.contexto());
        return result;
    }
}
