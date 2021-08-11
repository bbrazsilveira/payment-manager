package br.com.bbrazsilveira.payment.v1.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BoletoCreatedDto {

    @ApiModelProperty(example = "7bf29eb3-4c4f-4bbe-a1f0-1ffc2b564ec9", notes = "Identificador único do boleto")
    private UUID id;

    @ApiModelProperty(notes = "URL de download do boleto em PDF")
    private String url;
}
