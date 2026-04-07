package com.smartscout.api.dto;

import lombok.Data;

@Data
public class ConfirmacaoPresencaRequest {
    private String jogadorId;
    private Boolean confirmado = false;
    private Integer minutosEstimados = 90;
}

