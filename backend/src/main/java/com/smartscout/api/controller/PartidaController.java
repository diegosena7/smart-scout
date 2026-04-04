package com.smartscout.api.controller;

import com.smartscout.api.dto.PartidaRequest;
import com.smartscout.api.entity.Partida;
import com.smartscout.api.service.PartidaService;
import com.smartscout.api.service.RelatorioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/partidas")
public class PartidaController {

    private final PartidaService partidaService;
    private final RelatorioService relatorio;

    public PartidaController(PartidaService partidaService, RelatorioService relatorio) {
        this.partidaService = partidaService;
        this.relatorio = relatorio;
    }

    @GetMapping
    public Map<String, Object> listar() {
        List<Partida> partidas = partidaService.listar();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", partidas);
        result.putAll(relatorio.contexto());
        return result;
    }

    @PostMapping
    public Map<String, Object> criar(@Valid @RequestBody PartidaRequest req) {
        Partida partida = partidaService.criar(req);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mensagem", "Partida cadastrada com sucesso.");
        result.put("partida_id", partida.getPartidaId());
        result.put("avisos", List.of());
        result.putAll(relatorio.contexto());
        return result;
    }
}
