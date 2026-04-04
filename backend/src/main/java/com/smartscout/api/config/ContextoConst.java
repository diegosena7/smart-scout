package com.smartscout.api.config;

import java.util.List;
import java.util.Map;

public final class ContextoConst {

    private ContextoConst() {}

    public static final List<String> POSICOES_VALIDAS = List.of(
            "Goleiro", "Zagueiro", "Lateral", "Volante", "Meia", "Atacante"
    );

    public static final List<String> RESULTADOS_VALIDOS = List.of("Vitoria", "Empate", "Derrota");

    public static final List<String> FUNCOES_TATICAS_VALIDAS = List.of(
            "Centroavante", "Falso 9", "Segundo atacante", "Ponta direita", "Ponta esquerda",
            "Meia-atacante", "Meia central", "Volante construtor", "Volante marcador",
            "Ala-direito", "Ala-esquerdo", "Lateral ofensivo", "Lateral equilibrado",
            "Zagueiro construtor", "Zagueiro de cobertura", "Goleiro linha", "Goleiro classico"
    );

    public static final List<String> PES_DOMINANTES_VALIDOS = List.of("Direito", "Esquerdo", "Ambidestro");

    public static final List<String> FORMACOES_DISPONIVEIS = List.of("4-3-3", "4-4-2", "4-2-3-1");

    public static final Map<String, Map<String, Integer>> FORMACOES_BASE = Map.of(
            "4-3-3", Map.of("Goleiro", 1, "Zagueiro", 2, "Lateral", 2, "Volante", 1, "Meia", 2, "Atacante", 3),
            "4-4-2", Map.of("Goleiro", 1, "Zagueiro", 2, "Lateral", 2, "Volante", 2, "Meia", 2, "Atacante", 2),
            "4-2-3-1", Map.of("Goleiro", 1, "Zagueiro", 2, "Lateral", 2, "Volante", 2, "Meia", 3, "Atacante", 1)
    );

    // Score weights — mirrors configuracao.py
    public static final double PESO_TITULARIDADE_DESEMPENHO = 0.79;
    public static final double PESO_TITULARIDADE_DISCIPLINA = 0.21;
    public static final double PESO_MOMENTO_TITULARIDADE = 0.70;
    public static final double PESO_MOMENTO_USO_RECENTE = 0.30;

    public static final double PESO_DISCIPLINA_AMARELO = 1.5;
    public static final double PESO_DISCIPLINA_VERMELHO = 6.0;
    public static final double PESO_DISCIPLINA_FALTA = 0.4;

    public static final double DELTA_GANHOU_ESPACO = 8.0;
    public static final double DELTA_PERDEU_ESPACO = -8.0;
}
