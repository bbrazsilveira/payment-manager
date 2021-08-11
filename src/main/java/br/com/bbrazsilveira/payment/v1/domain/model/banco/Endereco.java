package br.com.bbrazsilveira.payment.v1.domain.model.banco;

import br.com.bbrazsilveira.payment.v1.domain.model.PObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.Arrays;

@Getter
@Setter
@Entity
@Table(name = "bnc_endereco")
public class Endereco extends PObject {

	@Column(nullable = false)
	private String logradouro;

	@Column
	private Integer numero;

	@Column(nullable = false)
	private String complemento;

	@Column(nullable = false)
	private String bairro;

	@Pattern(regexp = "\\d{8}")
	@Column(length = 8, nullable = false)
	private String cep;

	@Column(nullable = false)
	private String cidade;

	@Column(length = 2, nullable = false)
	private String uf;

	@Transient
	public String getEndereco() {
		if (numero == null) {
			return String.join(", ", Arrays.asList(logradouro, complemento));
		} else {
			return String.join(", ", Arrays.asList(logradouro, String.valueOf(numero), complemento));
		}
	}

	@PreUpdate
	@PrePersist
	private void prePersistAndUpdate() {
		cep = cep.replaceAll("\\D+","");
	}
}
