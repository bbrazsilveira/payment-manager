package br.com.bbrazsilveira.payment.v1.repository;


import br.com.bbrazsilveira.payment.v1.domain.model.arquivo.Arquivo;
import br.com.bbrazsilveira.payment.v1.domain.model.banco.Convenio;
import br.com.bbrazsilveira.payment.v1.domain.model.boleto.Boleto;
import br.com.bbrazsilveira.payment.v1.domain.model.boleto.BoletoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArquivoRepository extends JpaRepository<Arquivo, UUID> {

    @Query(value = "SELECT nextval('seq_arquivo')", nativeQuery = true)
    Integer getNextNumeroArquivo();
}