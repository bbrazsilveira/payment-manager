package br.com.bbrazsilveira.payment.v1.repository;


import br.com.bbrazsilveira.payment.v1.domain.model.boleto.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, UUID> {

    @Query(value = "select " +
            "  boleto.* " +
            "from " +
            "  blt_boleto boleto " +
            "  inner join blt_boleto_status status on status.boleto_id = boleto.id " +
            "where " +
            "  boleto.convenio_id = ?1 " +
            "  and boleto.deleted_by is null " +
            "  and status.deleted_by is null " +
            "  and status.status = ?2", nativeQuery = true)
    List<Boleto> findAllActiveByConvenioIdAndStatus(UUID convenioId, String status);

    @Query(value = "SELECT nextval('seq_nosso_numero')", nativeQuery = true)
    Integer getNextNossoNumero();
}