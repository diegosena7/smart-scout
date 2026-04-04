package com.smartscout.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AtuacaoRequest {
    @NotBlank
    private String partidaId;
    @NotBlank
    private String jogadorId;
    private String jogador = "";
    private String posicaoJogo = "";
    private int minutosJogados = 0;
    private int gols = 0;
    private int assistencias = 0;
    private int finalizacoes = 0;
    private int finalizacoesNoAlvo = 0;
    private int passesDecisivos = 0;
    private int passesCertos = 0;
    private int desarmes = 0;
    private int duelosGanhos = 0;
    private int interceptacoes = 0;
    private int recuperacoesBola = 0;
    private int cruzamentosCertos = 0;
    private int defesasDificeis = 0;
    private int golsSofridos = 0;
    private int jogosSemSofrerGols = 0;
    private int cartoesAmarelos = 0;
    private int cartoesVermelhos = 0;
    private int faltasCometidas = 0;
}
