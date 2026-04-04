package com.smartscout.api.controller;

import com.smartscout.api.service.RelatorioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class DashboardController {

    private final RelatorioService relatorio;

    public DashboardController(RelatorioService relatorio) {
        this.relatorio = relatorio;
    }

    @GetMapping("/dashboard/overview")
    public Map<String, Object> overview() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.putAll(relatorio.dashboard());
        result.putAll(relatorio.contexto());
        return result;
    }
}
