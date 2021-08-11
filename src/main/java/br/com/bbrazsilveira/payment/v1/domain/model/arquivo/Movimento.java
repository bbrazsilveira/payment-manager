package br.com.bbrazsilveira.payment.v1.domain.model.arquivo;

import br.com.bbrazsilveira.payment.v1.domain.model.PObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "arq_movimento", indexes = {
		@Index(columnList = "codigo")
})
public class Movimento extends PObject {

	@Column(nullable = false)
	private Integer codigo;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Arquivo arquivo;

}
