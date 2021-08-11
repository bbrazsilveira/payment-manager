package br.com.bbrazsilveira.payment.v1.domain.model.conta;

import br.com.bbrazsilveira.payment.v1.domain.model.PObject;
import br.com.bbrazsilveira.payment.v1.repository.UserRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "cnt_user")
public class User extends PObject {
}
