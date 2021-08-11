package br.com.bbrazsilveira.payment.v1.domain.model.boleto;

import br.com.bbrazsilveira.payment.v1.domain.model.PObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "blt_boleto_status", indexes = {
        @Index(columnList = "status")
})
public class BoletoStatus extends PObject {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(columnDefinition = "timestamp default now()", insertable = false, updatable = false, nullable = false)
    private LocalDateTime dataStatus;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Boleto boleto;

    public BoletoStatus() {
    }

    public BoletoStatus(Status status, Boleto boleto) {
        this.status = status;
        this.boleto = boleto;
    }

    public enum Status {
        CRIADO, AGUARDANDO_PAGAMENTO, LIQUIDADO, CANCELADO, RECUSADO
    }
}
