package com.smartscout.api.controller;

import com.smartscout.api.dto.ConfirmacaoPresencaRequest;
import com.smartscout.api.entity.ConfirmacaoPresenca;
import com.smartscout.api.service.ConfirmacaoPresencaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/confirmacao-presenca")
public class ConfirmacaoPresencaController {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmacaoPresencaController.class);
    private final ConfirmacaoPresencaService service;

    public ConfirmacaoPresencaController(ConfirmacaoPresencaService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> listarPorPartida(@RequestParam String partidaId) {
        logger.info("[REQUEST] GET /confirmacao-presenca?partida_id={}", partidaId);
        List<ConfirmacaoPresenca> confirmacoes = service.listarPorPartida(partidaId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", confirmacoes);
        logger.info("[RESPONSE] GET /confirmacao-presenca - {} confirmações retornadas", confirmacoes.size());
        return result;
    }

    @GetMapping("/confirmados")
    public Map<String, Object> listarConfirmados(@RequestParam String partidaId) {
        logger.info("[REQUEST] GET /confirmacao-presenca/confirmados?partida_id={}", partidaId);
        List<ConfirmacaoPresenca> confirmacoes = service.listarConfirmadosPorPartida(partidaId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("items", confirmacoes);
        logger.info("[RESPONSE] GET /confirmacao-presenca/confirmados - {} confirmados retornados", confirmacoes.size());
        return result;
    }

    @PostMapping("/confirmar")
    public Map<String, Object> confirmar(@RequestParam String partidaId,
                                           @RequestParam String jogadorId,
                                           @Valid @RequestBody ConfirmacaoPresencaRequest req) {
        logger.info("[REQUEST] POST /confirmacao-presenca/confirmar - partida: {}, jogador: {}, confirmado: {}", 
            partidaId, jogadorId, req.getConfirmado());
        service.confirmar(partidaId, jogadorId, req);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mensagem", "Presenca confirmada com sucesso.");
        logger.info("[RESPONSE] POST /confirmacao-presenca/confirmar - Presença confirmada com sucesso");
        return result;
    }

    @PostMapping("/confirmar-lote")
    public Map<String, Object> confirmarLote(@RequestParam String partidaId,
                                              @Valid @RequestBody List<ConfirmacaoPresencaRequest> reqs) {
        logger.info("[REQUEST] POST /confirmacao-presenca/confirmar-lote - partida: {}, {} confirmações", 
            partidaId, reqs.size());
        service.confirmarLote(partidaId, reqs);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mensagem", "Presencas confirmadas com sucesso para " + reqs.size() + " jogadores.");
        logger.info("[RESPONSE] POST /confirmacao-presenca/confirmar-lote - {} presencas confirmadas", reqs.size());
        return result;
    }

    @DeleteMapping
    public Map<String, Object> excluirPorPartida(@RequestParam String partidaId) {
        logger.info("[REQUEST] DELETE /confirmacao-presenca?partida_id={}", partidaId);
        service.excluirPorPartida(partidaId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("mensagem", "Confirmacoes de presenca excluidas.");
        logger.info("[RESPONSE] DELETE /confirmacao-presenca - Confirmações excluídas");
        return result;
    }
}

