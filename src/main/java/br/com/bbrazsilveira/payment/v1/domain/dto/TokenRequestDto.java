package br.com.bbrazsilveira.payment.v1.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class TokenRequestDto implements Serializable {

    @NotNull
    @Pattern(regexp = "password|refresh_token", flags = Pattern.Flag.CASE_INSENSITIVE)
    @ApiModelProperty(example = "password", allowableValues = "password,refresh_token", notes = "Grant type. Tipo de autenticação: \"password\" usuário e senha, ou \"refresh_token\" token de atualização.", required = true)
    private String grantType;

    @ApiModelProperty(example = "user", notes = "Username do usuário. Campo obrigatório se grant type é \"password\"")
    private String username;

    @ApiModelProperty(example = "012345", notes = "Senha do usuário. Campo obrigatório se grant type é \"password\"")
    private String password;

    @ApiModelProperty(example = "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAi...", notes = "Refresh token obtido previamente na autenticação. Campo obrigatório se grant type é \"refresh_token\"")
    private String refreshToken;
}