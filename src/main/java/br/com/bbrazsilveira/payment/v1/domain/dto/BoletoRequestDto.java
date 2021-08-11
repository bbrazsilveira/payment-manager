package br.com.bbrazsilveira.payment.v1.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class BoletoRequestDto {

    @NotBlank
    @Size(min = 3, max = 50)
    @Pattern(regexp = "[\\w\\s-()]+")
    @ApiModelProperty(required = true, example = "Boleto-015206", notes = "Nome do arquivo PDF do boleto")
    private String nome;

    @NotNull
    @ApiModelProperty(required = true, example = "96dd1573-594a-45d5-a2c7-fe003fed02c9", notes = "Identificador único do convênio responsável pela geração do boleto")
    private UUID convenioId;

    @NotNull
    @ApiModelProperty(required = true)
    private @Valid TituloDto titulo;

    @NotNull
    @ApiModelProperty(required = true)
    private @Valid PagadorDto pagador;

    @NotNull
    @AssertFalse
    @ApiModelProperty(required = true, example = "false")
    private Boolean aceite;

    @NotNull
    @ApiModelProperty(required = true, example = "REAL")
    private Especie especieMoeda;

    @NotNull
    @FutureOrPresent
    @ApiModelProperty(required = true, example = "2020-01-31", notes = "A data de vencimento do boleto tem que ser maior ou igual a data atual")
    private LocalDate dataVencimento;

    @NotBlank
    @Size(min = 3, max = 255)
    @ApiModelProperty(required = true, example = "Pagável em qualquer banco até o vencimento")
    private String localPagamento;

    @Size(max = 255)
    @ApiModelProperty(example = "Não receber após vencimento.", notes = "Linha 1 de instruções")
    private String instrucao1;

    @Size(max = 255)
    @ApiModelProperty(notes = "Linha 2 de instruções")
    private String instrucao2;

    @Size(max = 255)
    @ApiModelProperty(notes = "Linha 3 de instruções")
    private String instrucao3;

    @Size(max = 255)
    @ApiModelProperty(notes = "Linha 4 de instruções")
    private String instrucao4;

    @Size(max = 255)
    @ApiModelProperty(notes = "Linha 5 de instruções")
    private String instrucao5;

    public enum Especie {
        REAL
    }

    @Data
    @ApiModel(value = "BoletoRequestDto.TituloDto")
    public static class TituloDto {
        @NotNull
        @ApiModelProperty(required = true, example = "DM")
        private Especie especie;

        @NotBlank
        @Size(min = 1, max = 15)
        @Pattern(regexp = "\\d+")
        @ApiModelProperty(required = true, example = "12345", notes = "Número identificador do título/documento")
        private String numero;

        @NotNull
        @Positive
        @Digits(integer = 6, fraction = 2)
        @ApiModelProperty(required = true, example = "14.90", notes = "Valor do boleto")
        private BigDecimal valor;

        @NotNull
        @ApiModelProperty(required = true, example = "2020-01-30", notes = "Data de criação do título/documento")
        private LocalDate dataTitulo;

        public enum Especie {
            DM
        }
    }

    @Data
    @ApiModel(value = "BoletoRequestDto.PagadorDto")
    public static class PagadorDto {
        @NotBlank
        @Size(min = 3, max = 255)
        @ApiModelProperty(required = true, example = "Bruno Braz")
        private String nome;

        @NotNull
        @ApiModelProperty(required = true, example = "CPF")
        private TipoDocumento tipoDocumento;

        @NotBlank
        @Pattern(regexp = "(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})|(\\d{11})")
        @ApiModelProperty(required = true, example = "059.893.186-42")
        private String documento;

        @NotBlank
        @Size(min = 3, max = 255)
        @ApiModelProperty(required = true, example = "Av Exemplo, 1050, Ed Metropolitan")
        private String endereco;

        @NotBlank
        @Size(min = 3, max = 255)
        @ApiModelProperty(required = true, example = "Centro")
        private String bairro;

        @NotBlank
        @Pattern(regexp = "(\\d{5}-\\d{3})|(\\d{8})")
        @ApiModelProperty(required = true, example = "74810-100")
        private String cep;

        @NotBlank
        @Size(min = 3, max = 255)
        @ApiModelProperty(required = true, example = "Goiânia")
        private String cidade;

        @NotNull
        @ApiModelProperty(required = true, example = "GO")
        private UF uf;

        public enum UF {
            AC, AL, AP, AM, BA, CE, DF, ES, GO, MA, MT, MS, MG, PA, PB, PR, PE, PI, PJ, RN, RS, RO, RR, SC, SP, SE, TO
        }

        public enum TipoDocumento {
            CPF
        }

        public String getDocumento() {
            return documento.replaceAll("\\D+", "");
        }

        public String getCep() {
            return cep.replaceAll("\\D+", "");
        }
    }
}