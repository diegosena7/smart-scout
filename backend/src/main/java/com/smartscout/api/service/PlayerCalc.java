package com.smartscout.api.service;

import lombok.Getter;
import lombok.Setter;

/**
 * Objeto mutável usado internamente pelo pipeline analítico.
 * Acumula stats brutas e recebe os scores computados em etapas.
 */
@Getter
@Setter
class PlayerCalc {

    // Identidade
    String jogadorId;
    String jogador;
    String posicao;

    // Stats agregadas
    int jogos;
    int minutosJogados;
    int minutosUltimos5Jogos;
    int gols;
    int assistencias;
    int finalizacoes;
    int finalizacoesNoAlvo;
    int passesDecisivos;
    int passesCertos;
    int desarmes;
    int duelosGanhos;
    int interceptacoes;
    int recuperacoesBola;
    int cruzamentosCertos;
    int defesasDificeis;
    int golsSofridos;
    int jogosSemSofrerGols;
    int cartoesAmarelos;
    int cartoesVermelhos;
    int faltasCometidas;

    // Métricas por 90 e taxas
    double golsPor90;
    double assistenciasPor90;
    double finalizacoesNoAlvoPor90;
    double taxaFinalizacoesNoAlvo;
    double taxaFinalizacoesNoAlvoPonderada;
    double passesDecisosPor90;
    double passesCertosPor90;
    double desarmesPor90;
    double duelosGanhosPor90;
    double interceptacoesPor90;
    double recuperacoesBolaPor90;
    double cruzamentosCertosPor90;
    double defesasDificeisPor90;
    double golsSofridosPor90;
    double golsSofridosPor90Invertido;
    double jogosSemSofrerGolsPorJogo;
    double jogosSemSofrerGolsPorJogoPonderado;

    // Scores computados
    double desempenhoBruto;
    double scoreDesempenho;
    double scoreDisciplina;
    double scoreNotaTecnico;  // Novo: score normalizado da nota do técnico (0-100)
    double scoreConfiabilidade;
    double scoreTitularidade;
    double indicUsoRecente;
    double scoreUsoRecente;
    double scoreMomento;
    double deltaMomento;
    String tendenciaRecente;
    int rankTitularidadePosicao;
    int rankMomentoPosicao;

    // Confiabilidade / presença
    double taxaPresenca;       // 0–1 (jogos / totalPartidas)
}
