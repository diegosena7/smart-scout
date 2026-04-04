package com.smartscout.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JogadorRequest {
    @NotBlank
    private String jogador;
    @NotBlank
    private String posicao;
    private String status = "Ativo";
    private String funcao = "";
    private String peDominante = "";
    private String observacoes = "";
}
