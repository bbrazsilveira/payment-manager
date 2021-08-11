package br.com.bbrazsilveira.payment.v1.repository;


import br.com.bbrazsilveira.payment.v1.domain.model.conta.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
}