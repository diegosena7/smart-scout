package com.smartscout.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "confirmacao_presenca", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"partida_id", "jogador_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConfirmacaoPresenca {

    @Id
    private String id;

    @Column(nullable = false)
    private String partidaId;

    @Column(nullable = false)
    private String jogadorId;

    @Builder.Default
    private Boolean confirmado = false;

    @Builder.Default
    private Integer minutosEstimados = 90;
}

