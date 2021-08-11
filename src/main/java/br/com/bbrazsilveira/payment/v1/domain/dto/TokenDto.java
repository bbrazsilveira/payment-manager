package br.com.bbrazsilveira.payment.v1.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TokenDto implements Serializable {

    @ApiModelProperty(example = "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAi...", notes = "Token de acesso", required = true)
    private String accessToken;

    @ApiModelProperty(example = "3600", notes = "Duração (em segundos) do token de acesso", required = true)
    private Long expiresIn;

    @ApiModelProperty(example = "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAi...", notes = "Refresh token", required = true)
    private String refreshToken;

    @ApiModelProperty(example = "86400", notes = "Duração (em segundos) do refresh token", required = true)
    private Long refreshExpiresIn;

    @ApiModelProperty(example = "bearer", notes = "Tipo do token", required = true)
    private String tokenType;
}
