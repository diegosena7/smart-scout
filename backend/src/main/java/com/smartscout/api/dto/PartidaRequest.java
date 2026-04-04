package com.smartscout.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PartidaRequest {
    @NotBlank
    private String dataPartida;
    @NotBlank
    private String adversario;
    private String competicao = "";
    private String mandante = "Casa";
    @NotBlank
    private String resultado;
    private String formacao = "";
    private String observacoes = "";
}
