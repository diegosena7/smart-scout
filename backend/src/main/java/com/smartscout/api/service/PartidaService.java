package com.smartscout.api.service;

import com.smartscout.api.dto.PartidaRequest;
import com.smartscout.api.entity.Partida;
import com.smartscout.api.repository.PartidaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class PartidaService {

    private final PartidaRepository repo;

    public PartidaService(PartidaRepository repo) {
        this.repo = repo;
    }

    public List<Partida> listar() {
        return repo.findAllByOrderByDataPartidaAsc();
    }

    public Partida criar(PartidaRequest req) {
        LocalDate data = LocalDate.parse(req.getDataPartida(), DateTimeFormatter.ISO_LOCAL_DATE);
        String id = gerarId(req.getDataPartida(), req.getAdversario());
        var partida = Partida.builder()
                .partidaId(id)
                .dataPartida(data)
                .adversario(req.getAdversario().trim())
                .competicao(req.getCompeticao() == null ? "" : req.getCompeticao())
                .mandante(req.getMandante() == null ? "Casa" : req.getMandante())
                .resultado(req.getResultado())
                .formacao(req.getFormacao() == null ? "" : req.getFormacao())
                .observacoes(req.getObservacoes() == null ? "" : req.getObservacoes())
                .build();
        return repo.save(partida);
    }

    private String gerarId(String data, String adversario) {
        String slug = adversario.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
        slug = slug.isEmpty() ? "jogo" : slug.substring(0, Math.min(6, slug.length()));
        return data.replace("-", "") + "-" + slug + "-" + UUID.randomUUID().toString().substring(0, 4);
    }
}
