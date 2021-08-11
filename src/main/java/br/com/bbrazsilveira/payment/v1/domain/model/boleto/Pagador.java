package br.com.bbrazsilveira.payment.v1.domain.model.boleto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@Embeddable
public class Pagador {

    @Column(name = "pagadorNome", nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "pagadorTipoDocumento", nullable = false)
    private TipoDocumento tipoDocumento;

    @Pattern(regexp = "\\d{11}")
    @Column(name = "pagadorDocumento", length = 11, nullable = false)
    private String documento;

    @Column(name = "pagadorEndereco", nullable = false)
    private String endereco;

    @Column(name = "pagadorBairro", nullable = false)
    private String bairro;

    @Pattern(regexp = "\\d{8}")
    @Column(name = "pagadorCep", length = 8, nullable = false)
    private String cep;

    @Column(name = "pagadorCidade", nullable = false)
    private String cidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "pagadorUf", length = 2, nullable = false)
    private UF uf;

    public enum TipoDocumento {
        CPF, CNPJ
    }

    public enum UF {
        AC, AL, AP, AM, BA, CE, DF, ES, GO, MA, MT, MS, MG, PA, PB, PR, PE, PI, PJ, RN, RS, RO, RR, SC, SP, SE, TO
    }

    @PreUpdate
    @PrePersist
    private void prePersistAndUpdate() {
        cep = cep.replaceAll("\\D+", "");
        documento = documento.replaceAll("\\D+", "");
    }
}
