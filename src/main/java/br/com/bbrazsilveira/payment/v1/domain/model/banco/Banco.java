package br.com.bbrazsilveira.payment.v1.domain.model.banco;

import br.com.bbrazsilveira.payment.v1.domain.model.PObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "bnc_banco")
public class Banco extends PObject {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 3)
    private String codigo;

    @Column(nullable = false)
    private String logo;

    public static final String CODIGO_SANTANDER = "033";
}
