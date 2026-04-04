package com.smartscout.api.service;

import com.smartscout.api.dto.JogadorRequest;
import com.smartscout.api.entity.Jogador;
import com.smartscout.api.repository.JogadorRepository;
import org.springframework.stereotype.Service;

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
}
