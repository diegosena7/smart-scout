package com.smartscout.api.repository;

import com.smartscout.api.entity.ConfirmacaoPresenca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConfirmacaoPresencaRepository extends JpaRepository<ConfirmacaoPresenca, String> {
    List<ConfirmacaoPresenca> findByPartidaId(String partidaId);
    List<ConfirmacaoPresenca> findByPartidaIdAndConfirmadoTrue(String partidaId);
    Optional<ConfirmacaoPresenca> findByPartidaIdAndJogadorId(String partidaId, String jogadorId);
}
