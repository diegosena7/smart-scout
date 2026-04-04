package com.smartscout.api.repository;

import com.smartscout.api.entity.Jogador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JogadorRepository extends JpaRepository<Jogador, String> {
    List<Jogador> findByJogadorContainingIgnoreCase(String nome);
    List<Jogador> findByStatus(String status);
}
