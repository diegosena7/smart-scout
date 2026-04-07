package com.smartscout.api.service;

import com.smartscout.api.dto.JogadorRequest;
import com.smartscout.api.entity.Jogador;
import com.smartscout.api.repository.JogadorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class JogadorService {

    private final JogadorRepository repo;

    public JogadorService(JogadorRepository repo) {
        this.repo = repo;
    }

    public List<Jogador> listar() {
        return repo.findAll();
    }

    public Jogador criar(JogadorRequest req) {
        var jogador = Jogador.builder()
                .jogadorId(UUID.randomUUID().toString())
                .jogador(req.getJogador().trim())
                .posicao(req.getPosicao())
                .status(req.getStatus() == null || req.getStatus().isBlank() ? "Ativo" : req.getStatus())
                .funcao(req.getFuncao() == null ? "" : req.getFuncao())
                .peDominante(req.getPeDominante() == null ? "" : req.getPeDominante())
                .observacoes(req.getObservacoes() == null ? "" : req.getObservacoes())
                .build();
        return repo.save(jogador);
    }

    public Jogador atualizar(String id, JogadorRequest req) {
        var jogador = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado: " + id));

        jogador.setJogador(req.getJogador().trim());
        jogador.setPosicao(req.getPosicao());
        jogador.setStatus(req.getStatus() == null || req.getStatus().isBlank() ? "Ativo" : req.getStatus());
        jogador.setFuncao(req.getFuncao() == null ? "" : req.getFuncao());
        jogador.setPeDominante(req.getPeDominante() == null ? "" : req.getPeDominante());
        jogador.setObservacoes(req.getObservacoes() == null ? "" : req.getObservacoes());

        return repo.save(jogador);
    }

    public void deletar(String id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado: " + id);
        }
        repo.deleteById(id);
    }
}

