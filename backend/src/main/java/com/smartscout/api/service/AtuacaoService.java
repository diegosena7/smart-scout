package com.smartscout.api.service;

import com.smartscout.api.dto.AtuacaoRequest;
import com.smartscout.api.entity.Atuacao;
import com.smartscout.api.repository.AtuacaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AtuacaoService {

    private final AtuacaoRepository repo;

    public AtuacaoService(AtuacaoRepository repo) {
        this.repo = repo;
    }

    public List<Atuacao> listar() {
        return repo.findAll();
    }

    public Atuacao criar(AtuacaoRequest req) {
        var a = fromRequest(UUID.randomUUID().toString(), req);
        return repo.save(a);
    }

    public Atuacao atualizar(String id, AtuacaoRequest req) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Atuacao nao encontrada: " + id);
        }
        return repo.save(fromRequest(id, req));
    }

    public void excluir(String id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Atuacao nao encontrada: " + id);
        }
        repo.deleteById(id);
    }

    private Atuacao fromRequest(String id, AtuacaoRequest req) {
        return Atuacao.builder()
                .atuacaoId(id)
                .partidaId(req.getPartidaId())
                .jogadorId(req.getJogadorId())
                .jogador(req.getJogador() == null ? "" : req.getJogador())
                .posicaoJogo(req.getPosicaoJogo() == null ? "" : req.getPosicaoJogo())
                .minutosJogados(req.getMinutosJogados())
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
}
