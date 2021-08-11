package br.com.bbrazsilveira.payment.v1.domain.model.banco;


import br.com.bbrazsilveira.payment.v1.domain.model.PObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Entity
@Table(name = "bnc_convenio")
public class Convenio extends PObject {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo tipo;

    @Pattern(regexp = "\\d+")
    @Column(nullable = false)
    private String numero;

    @Pattern(regexp = "\\d+")
    @Column(nullable = false)
    private String carteira;

    @Pattern(regexp = "\\d+")
    @Column(nullable = false)
    private String agencia;

    @Pattern(regexp = "\\d")
    @Column(length = 1, nullable = false)
    private String agenciaDigito;

    @Pattern(regexp = "\\d+")
    @Column(nullable = false)
    private String contaCorrente;

    @Pattern(regexp = "\\d")
    @Column(length = 1, nullable = false)
    private String contaCorrenteDigito;

    @Column(nullable = false)
    private String codigoTransmissao;

    @Column(nullable = false)
    private String templateBoletoPath;

    @Column(nullable = false)
    private String templateArquivoPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormatoArquivo templateArquivoFormato;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Beneficiario beneficiario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Banco banco;

    public enum Tipo {
        COBRANCA, PAGAMENTO
    }

    public enum FormatoArquivo {
        CNAB240
    }
}
