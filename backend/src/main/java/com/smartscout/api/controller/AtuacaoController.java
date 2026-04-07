package com.smartscout.api.controller;

import com.smartscout.api.dto.AtuacaoRequest;
import com.smartscout.api.entity.Atuacao;
import com.smartscout.api.service.AtuacaoService;
import com.smartscout.api.service.RelatorioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/atuacoes")
public class AtuacaoController {

    private static final Logger logger = LoggerFactory.getLogger(AtuacaoController.class);
    private final AtuacaoService atuacaoService;
    private final RelatorioService relatorio;

    public AtuacaoController(AtuacaoService atuacaoService, RelatorioService relatorio) {
        this.atuacaoService = atuacaoService;
        this.relatorio = relatorio;
    }

    @GetMapping
    public Map<String, Object> listar() {
        logger.info("[REQUEST] GET /atuacoes");
        List<Atuacao> atuacoes = atuacaoService.listar();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", atuacoes);
        result.putAll(relatorio.contexto());
        logger.info("[RESPONSE] GET /atuacoes - {} atuações retornadas", atuacoes.size());
        return result;
    }

    @PostMapping
    public Map<String, Object> criar(@Valid @RequestBody AtuacaoRequest req) {
        logger.info("[REQUEST] POST /atuacoes - Criando atuação para jogador: {}, partida: {}, presença: {}",
            req.getJogadorId(), req.getPartidaId(), req.getPresenca());
        atuacaoService.criar(req);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mensagem", "Atuacao cadastrada com sucesso.");
        result.put("avisos", List.of());
        result.putAll(relatorio.contexto());
        logger.info("[RESPONSE] POST /atuacoes - Atuação criada com sucesso");
        return result;
    }

    @PutMapping("/{id}")
    public Map<String, Object> atualizar(@PathVariable String id,
                                          @Valid @RequestBody AtuacaoRequest req) {
        logger.info("[REQUEST] PUT /atuacoes/{} - Atualizando atuação. Presença: {}, Nota: {}",
            id, req.getPresenca(), req.getNotaTecnico());
        atuacaoService.atualizar(id, req);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mensagem", "Atuacao atualizada com sucesso.");
        result.put("avisos", List.of());
        result.putAll(relatorio.contexto());
        logger.info("[RESPONSE] PUT /atuacoes/{} - Atuação atualizada com sucesso", id);
        return result;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> excluir(@PathVariable String id) {
        logger.info("[REQUEST] DELETE /atuacoes/{}", id);
        atuacaoService.excluir(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mensagem", "Atuacao excluida com sucesso.");
        result.putAll(relatorio.contexto());
        logger.info("[RESPONSE] DELETE /atuacoes/{} - Atuação deletada com sucesso", id);
        return result;
    }
}
