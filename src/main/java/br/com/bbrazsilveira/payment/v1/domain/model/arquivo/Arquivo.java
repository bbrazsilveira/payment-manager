package br.com.bbrazsilveira.payment.v1.domain.model.arquivo;

import br.com.bbrazsilveira.payment.v1.domain.model.PObject;
import br.com.bbrazsilveira.payment.v1.domain.model.banco.Convenio;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "arq_arquivo", indexes = {
        @Index(columnList = "tipo"),
        @Index(columnList = "numero")
})
public class Arquivo extends PObject {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo tipo;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false)
    private String url;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Convenio convenio;

    public enum Tipo {
        REMESSA, RETORNO
    }
}
