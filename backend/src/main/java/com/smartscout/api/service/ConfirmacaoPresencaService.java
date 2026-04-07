package com.smartscout.api.service;

import com.smartscout.api.dto.ConfirmacaoPresencaRequest;
import com.smartscout.api.entity.ConfirmacaoPresenca;
import com.smartscout.api.repository.ConfirmacaoPresencaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConfirmacaoPresencaService {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmacaoPresencaService.class);
    private final ConfirmacaoPresencaRepository repo;

    public ConfirmacaoPresencaService(ConfirmacaoPresencaRepository repo) {
        this.repo = repo;
    }

    public List<ConfirmacaoPresenca> listarPorPartida(String partidaId) {
        logger.info("GET /confirmacao-presenca - Listando confirmações da partida: {}", partidaId);
        List<ConfirmacaoPresenca> confirmacoes = repo.findByPartidaId(partidaId);
        logger.info("GET /confirmacao-presenca - Encontradas {} confirmações de presença", confirmacoes.size());
        return confirmacoes;
    }

    public List<ConfirmacaoPresenca> listarConfirmadosPorPartida(String partidaId) {
        logger.info("GET /confirmacao-presenca/confirmados - Listando CONFIRMADOS da partida: {}", partidaId);
        List<ConfirmacaoPresenca> confirmados = repo.findByPartidaIdAndConfirmadoTrue(partidaId);
        logger.info("GET /confirmacao-presenca/confirmados - {} jogadores confirmados para a partida", confirmados.size());
        return confirmados;
    }

    public ConfirmacaoPresenca confirmar(String partidaId, String jogadorId, ConfirmacaoPresencaRequest req) {
        logger.info("POST /confirmacao-presenca/confirmar - Confirmando presença. Partida: {}, Jogador: {}, Confirmado: {}, Minutos: {}", 
            partidaId, jogadorId, req.getConfirmado(), req.getMinutosEstimados());
        try {
            var existing = repo.findByPartidaIdAndJogadorId(partidaId, jogadorId);
            
            var confirmacao = existing.orElse(
                ConfirmacaoPresenca.builder()
                    .id(UUID.randomUUID().toString())
                    .partidaId(partidaId)
                    .jogadorId(jogadorId)
                    .build()
            );

            confirmacao.setConfirmado(req.getConfirmado());
            confirmacao.setMinutosEstimados(req.getMinutosEstimados() != null ? req.getMinutosEstimados() : 90);
            
            ConfirmacaoPresenca resultado = repo.save(confirmacao);
            logger.info("POST /confirmacao-presenca/confirmar - Presença confirmada com sucesso. ID: {}", resultado.getId());
            return resultado;
        } catch (Exception e) {
            logger.error("POST /confirmacao-presenca/confirmar - Erro ao confirmar presença: ", e);
            throw e;
        }
    }

    public void confirmarLote(String partidaId, List<ConfirmacaoPresencaRequest> reqs) {
        logger.info("POST /confirmacao-presenca/confirmar-lote - Confirmando {} jogadores para partida: {}", reqs.size(), partidaId);
        try {
            List<ConfirmacaoPresenca> confirmacoes = reqs.stream()
                .map(req -> {
                    var existing = repo.findByPartidaIdAndJogadorId(partidaId, req.getJogadorId());
                    var confirmacao = existing.orElse(
                        ConfirmacaoPresenca.builder()
                            .id(UUID.randomUUID().toString())
                            .partidaId(partidaId)
                            .jogadorId(req.getJogadorId())
                            .build()
                    );
                    confirmacao.setConfirmado(req.getConfirmado());
                    confirmacao.setMinutosEstimados(req.getMinutosEstimados() != null ? req.getMinutosEstimados() : 90);
                    logger.debug("Processando confirmação para jogador: {}, confirmado: {}", req.getJogadorId(), req.getConfirmado());
                    return confirmacao;
                })
                .toList();
            
            repo.saveAll(confirmacoes);
            logger.info("POST /confirmacao-presenca/confirmar-lote - {} confirmações salvas com sucesso", confirmacoes.size());
        } catch (Exception e) {
            logger.error("POST /confirmacao-presenca/confirmar-lote - Erro ao confirmar lote: ", e);
            throw e;
        }
    }

    public void excluirPorPartida(String partidaId) {
        logger.info("DELETE /confirmacao-presenca - Deletando todas as confirmações da partida: {}", partidaId);
        try {
            List<ConfirmacaoPresenca> todos = repo.findByPartidaId(partidaId);
            logger.info("DELETE /confirmacao-presenca - Encontradas {} confirmações para deletar", todos.size());
            repo.deleteAll(todos);
            logger.info("DELETE /confirmacao-presenca - Confirmações deletadas com sucesso");
        } catch (Exception e) {
            logger.error("DELETE /confirmacao-presenca - Erro ao deletar confirmações: ", e);
            throw e;
        }
    }
}

