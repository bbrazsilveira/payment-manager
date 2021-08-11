package br.com.bbrazsilveira.payment.v1.domain.model.boleto;

import br.com.bbrazsilveira.payment.v1.domain.model.PObject;
import br.com.bbrazsilveira.payment.v1.domain.model.banco.Convenio;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@Setter
@Entity
@Table(name = "blt_boleto")
public class Boleto extends PObject {

    @Column(nullable = false)
    private String nome;

    @Valid
    @Embedded
    private Pagador pagador;

    @Valid
    @Embedded
    private Titulo titulo;

    @Column(nullable = false)
    private Boolean aceite;

    @Column(nullable = false)
    private String especieMoeda;

    @Column(nullable = false)
    private String localPagamento;

    @Column(nullable = false)
    private LocalDate dataProcessamento;

    @Column(nullable = false)
    private LocalDate dataDocumento;

    @Column(nullable = false)
    private LocalDate dataVencimento;

    @Column
    private String instrucao1;

    @Column
    private String instrucao2;

    @Column
    private String instrucao3;

    @Column
    private String instrucao4;

    @Column
    private String instrucao5;

    @Column(nullable = false)
    private String url;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Convenio convenio;

    @Transient
    public String[] getInstrucoes() {
        return Stream.of(instrucao1, instrucao2, instrucao3, instrucao4, instrucao5)
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }
}

