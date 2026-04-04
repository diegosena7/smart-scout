package com.smartscout.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jogadores")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Jogador {

    @Id
    private String jogadorId;

    @Column(nullable = false)
    private String jogador;

    @Column(nullable = false)
    private String posicao;

    @Builder.Default
    private String status = "Ativo";

    @Builder.Default
    private String funcao = "";

    @Builder.Default
    private String peDominante = "";

    @Column(length = 1000)
    @Builder.Default
    private String observacoes = "";
}
