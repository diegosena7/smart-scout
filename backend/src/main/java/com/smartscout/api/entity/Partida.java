package com.smartscout.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "partidas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Partida {

    @Id
    private String partidaId;

    @Column(nullable = false)
    private LocalDate dataPartida;

    @Column(nullable = false)
    private String adversario;

    @Builder.Default
    private String competicao = "";

    @Builder.Default
    private String mandante = "Casa";

    @Column(nullable = false)
    private String resultado;

    @Builder.Default
    private String formacao = "";

    @Column(length = 1000)
    @Builder.Default
    private String observacoes = "";
}
