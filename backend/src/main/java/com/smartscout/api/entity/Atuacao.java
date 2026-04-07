package com.smartscout.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "atuacoes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Atuacao {

    @Id
    private String atuacaoId;

    @Column(nullable = false)
    private String partidaId;

    @Column(nullable = false)
    private String jogadorId;

    private String jogador;
    private String posicaoJogo;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Presenca presenca = Presenca.JOGOU;

    @Builder.Default
    private Integer notaTecnico = null;

    @Builder.Default private int minutosJogados = 0;
    @Builder.Default private int gols = 0;
    @Builder.Default private int assistencias = 0;
    @Builder.Default private int finalizacoes = 0;
    @Builder.Default private int finalizacoesNoAlvo = 0;
    @Builder.Default private int passesDecisivos = 0;
    @Builder.Default private int passesCertos = 0;
    @Builder.Default private int desarmes = 0;
    @Builder.Default private int duelosGanhos = 0;
    @Builder.Default private int interceptacoes = 0;
    @Builder.Default private int recuperacoesBola = 0;
    @Builder.Default private int cruzamentosCertos = 0;
    @Builder.Default private int defesasDificeis = 0;
    @Builder.Default private int golsSofridos = 0;
    @Builder.Default private int jogosSemSofrerGols = 0;
    @Builder.Default private int cartoesAmarelos = 0;
    @Builder.Default private int cartoesVermelhos = 0;
    @Builder.Default private int faltasCometidas = 0;

    public enum Presenca {
        JOGOU, PARCIAL, NAO_JOGOU
    }
}
