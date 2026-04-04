package com.smartscout.api.repository;

import com.smartscout.api.entity.Atuacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtuacaoRepository extends JpaRepository<Atuacao, String> {
    List<Atuacao> findByJogadorId(String jogadorId);
    List<Atuacao> findByPartidaId(String partidaId);
}
