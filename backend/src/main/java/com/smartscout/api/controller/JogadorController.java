package com.smartscout.api.controller;

import com.smartscout.api.dto.JogadorRequest;
import com.smartscout.api.entity.Jogador;
import com.smartscout.api.service.JogadorService;
import com.smartscout.api.service.RelatorioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jogadores")
public class JogadorController {

    private final JogadorService jogadorService;
    private final RelatorioService relatorio;

    public JogadorController(JogadorService jogadorService, RelatorioService relatorio) {
        this.jogadorService = jogadorService;
        this.relatorio = relatorio;
    }

    @GetMapping
    public Map<String, Object> listar() {
        List<Jogador> jogadores = jogadorService.listar();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", jogadores);
        result.putAll(relatorio.contexto());
        return result;
    }

    @PostMapping
    public Map<String, Object> criar(@Valid @RequestBody JogadorRequest req) {
        jogadorService.criar(req);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mensagem", "Jogador cadastrado com sucesso.");
        result.put("avisos", List.of());
        result.putAll(relatorio.contexto());
        return result;
    }
}
