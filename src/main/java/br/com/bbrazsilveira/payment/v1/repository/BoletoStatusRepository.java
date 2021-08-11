package br.com.bbrazsilveira.payment.v1.repository;


import br.com.bbrazsilveira.payment.v1.domain.model.boleto.BoletoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface BoletoStatusRepository extends JpaRepository<BoletoStatus, UUID> {

    @Modifying
    @Query(value = "update blt_boleto_status " +
            "set deleted_by = ?2, deleted_at = now() " +
            "where deleted_by is null and boleto_id in ?1", nativeQuery = true)
    void deleteAllByBoletos(UUID[] boletosId, UUID deletedBy);
}