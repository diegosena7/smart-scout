package com.smartscout.api.service;

import com.smartscout.api.dto.AtuacaoRequest;
import com.smartscout.api.entity.Atuacao;
import com.smartscout.api.repository.AtuacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AtuacaoService {

    private static final Logger logger = LoggerFactory.getLogger(AtuacaoService.class);
    private final AtuacaoRepository repo;

    public AtuacaoService(AtuacaoRepository repo) {
        this.repo = repo;
    }

    public List<Atuacao> listar() {
        logger.info("GET /atuacoes - Listando todas as atuações");
        List<Atuacao> atuacoes = repo.findAll();
        logger.info("GET /atuacoes - Encontradas {} atuações", atuacoes.size());
        return atuacoes;
    }

    public Atuacao criar(AtuacaoRequest req) {
        logger.info("POST /atuacoes - Criando nova atuação para jogador: {}, partida: {}, presença: {}, nota: {}",
                req.getJogadorId(), req.getPartidaId(), req.getPresenca(), req.getNotaTecnico());

        // Validar: jogador não pode aparecer 2x na mesma partida
        long countDuplicate = repo.findAll().stream()
                .filter(a -> a.getJogadorId().equals(req.getJogadorId()) && a.getPartidaId().equals(req.getPartidaId()))
                .count();

        if (countDuplicate > 0) {
            logger.warn("POST /atuacoes - Tentativa de duplicar jogador {} na partida {}", req.getJogadorId(), req.getPartidaId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Jogador já possui registro de atuação nesta partida. Edite o registro existente.");
        }

        try {
            var a = fromRequest(UUID.randomUUID().toString(), req);
            Atuacao resultado = repo.save(a);
            logger.info("POST /atuacoes - Atuação criada com sucesso. ID: {}, Minutos: {}, Presença: {}",
                    resultado.getAtuacaoId(), resultado.getMinutosJogados(), resultado.getPresenca());
            return resultado;
        } catch (Exception e) {
            logger.error("POST /atuacoes - Erro ao criar atuação: ", e);
            throw e;
        }
    }

    public Atuacao atualizar(String id, AtuacaoRequest req) {
        logger.info("PUT /atuacoes/{} - Atualizando atuação. Presença: {}, Nota: {}", id, req.getPresenca(), req.getNotaTecnico());
        if (!repo.existsById(id)) {
            logger.warn("PUT /atuacoes/{} - Atuação não encontrada", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Atuacao nao encontrada: " + id);
        }
        try {
            Atuacao resultado = repo.save(fromRequest(id, req));
            logger.info("PUT /atuacoes/{} - Atuação atualizada com sucesso. Minutos: {}, Presença: {}",
                    id, resultado.getMinutosJogados(), resultado.getPresenca());
            return resultado;
        } catch (Exception e) {
            logger.error("PUT /atuacoes/{} - Erro ao atualizar atuação: ", id, e);
            throw e;
        }
    }

    public void excluir(String id) {
        logger.info("DELETE /atuacoes/{} - Deletando atuação", id);
        if (!repo.existsById(id)) {
            logger.warn("DELETE /atuacoes/{} - Atuação não encontrada", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Atuacao nao encontrada: " + id);
        }
        try {
            repo.deleteById(id);
            logger.info("DELETE /atuacoes/{} - Atuação deletada com sucesso", id);
        } catch (Exception e) {
            logger.error("DELETE /atuacoes/{} - Erro ao deletar atuação: ", id, e);
            throw e;
        }
    }

    private Atuacao fromRequest(String id, AtuacaoRequest req) {
        validatePresenca(req.getPresenca());
        validateNotaTecnico(req.getNotaTecnico());

        int minutosJogados = req.getMinutosJogados();

        // Auto-zero minutos if NAO_JOGOU
        if ("NAO_JOGOU".equals(req.getPresenca())) {
            logger.debug("Zerando minutos para atuação com presença NAO_JOGOU");
            minutosJogados = 0;
        }

        return Atuacao.builder()
                .atuacaoId(id)
                .partidaId(req.getPartidaId())
                .jogadorId(req.getJogadorId())
                .jogador(req.getJogador() == null ? "" : req.getJogador())
                .posicaoJogo(req.getPosicaoJogo() == null ? "" : req.getPosicaoJogo())
                .presenca(Atuacao.Presenca.valueOf(req.getPresenca()))
                .notaTecnico(req.getNotaTecnico())
                .minutosJogados(minutosJogados)
                .gols(req.getGols())
                .assistencias(req.getAssistencias())
                .finalizacoes(req.getFinalizacoes())
                .finalizacoesNoAlvo(req.getFinalizacoesNoAlvo())
                .passesDecisivos(req.getPassesDecisivos())
                .passesCertos(req.getPassesCertos())
                .desarmes(req.getDesarmes())
                .duelosGanhos(req.getDuelosGanhos())
                .interceptacoes(req.getInterceptacoes())
                .recuperacoesBola(req.getRecuperacoesBola())
                .cruzamentosCertos(req.getCruzamentosCertos())
                .defesasDificeis(req.getDefesasDificeis())
                .golsSofridos(req.getGolsSofridos())
                .jogosSemSofrerGols(req.getJogosSemSofrerGols())
                .cartoesAmarelos(req.getCartoesAmarelos())
                .cartoesVermelhos(req.getCartoesVermelhos())
                .faltasCometidas(req.getFaltasCometidas())
                .build();
    }

    private void validatePresenca(String presenca) {
        if (presenca == null) return;
        try {
            Atuacao.Presenca.valueOf(presenca);
        } catch (IllegalArgumentException e) {
            logger.warn("Presença inválida fornecida: {}", presenca);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Presenca invalida: " + presenca);
        }
    }

    private void validateNotaTecnico(Integer nota) {
        if (nota == null) return;
        if (nota < 1 || nota > 5) {
            logger.warn("Nota do técnico inválida fornecida: {}", nota);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nota do tecnico deve estar entre 1 e 5");
        }
    }
}

