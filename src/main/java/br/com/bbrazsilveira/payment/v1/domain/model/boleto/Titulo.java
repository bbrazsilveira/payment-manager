package br.com.bbrazsilveira.payment.v1.domain.model.boleto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Embeddable
public class Titulo {

    @Column(name = "tituloNumero", nullable = false)
    private String numero;

    @Column(name = "tituloCodigo", nullable = false) // Nosso número
    private String codigo;

    @Pattern(regexp = "\\d")
    @Column(name = "tituloCodigoDigito", length = 1, nullable = false) // Nosso número
    private String codigoDigito;

    @Column(name = "tituloValor", nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "tituloEspecie", nullable = false)
    private String especie;

    @Column(name = "tituloData", nullable = false)
    private LocalDate dataTitulo;
}
