package br.com.bbrazsilveira.payment.v1.domain.model.banco;

import br.com.bbrazsilveira.payment.v1.domain.model.PObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Entity
@Table(name = "bnc_beneficiario")
public class Beneficiario extends PObject {

    @Column(nullable = false)
    private String nome;

    @Pattern(regexp = "\\d{14}")
    @Column(length = 14, nullable = false)
    private String documento;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Endereco endereco;

    @PreUpdate
    @PrePersist
    private void prePersistAndUpdate() {
        documento = documento.replaceAll("\\D+", "");
    }
}
