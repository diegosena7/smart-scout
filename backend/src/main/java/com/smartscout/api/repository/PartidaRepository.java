package com.smartscout.api.repository;

import com.smartscout.api.entity.Partida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartidaRepository extends JpaRepository<Partida, String> {
    List<Partida> findAllByOrderByDataPartidaAsc();
}
