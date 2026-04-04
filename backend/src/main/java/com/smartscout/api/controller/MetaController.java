package com.smartscout.api.controller;

import com.smartscout.api.service.RelatorioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MetaController {

    private final RelatorioService relatorio;

    public MetaController(RelatorioService relatorio) {
        this.relatorio = relatorio;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/meta/contexto")
    public Map<String, Object> contexto() {
        return relatorio.contexto();
    }
}
