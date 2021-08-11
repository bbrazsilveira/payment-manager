package br.com.bbrazsilveira.payment.v1.repository;


import br.com.bbrazsilveira.payment.v1.domain.model.banco.Convenio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface ConvenioRepository extends JpaRepository<Convenio, UUID> {

    Stream<Convenio> findAllByDeletedByNull();
}