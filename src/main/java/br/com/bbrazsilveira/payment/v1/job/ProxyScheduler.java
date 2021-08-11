package br.com.bbrazsilveira.payment.v1.job;

import br.com.bbrazsilveira.payment.v1.configuration.multitenancy.TenantContext;
import br.com.bbrazsilveira.payment.v1.configuration.security.UserContext;
import br.com.bbrazsilveira.payment.v1.domain.model.conta.User;
import br.com.bbrazsilveira.payment.v1.job.worker.RemessaWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

/**
 * Run jobs async on tenant context
 */
@Service
public class ProxyScheduler {

    @Value("${app.keycloak.admin-id}")
    private UUID adminId;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RemessaWorker remessaWorker;

    @Async
    public void enviarRemessasAsync(String tenant) {
        // Set current tenant and user
        TenantContext.setCurrentTenant(tenant);
        UserContext.setCurrentUser(entityManager.getReference(User.class, adminId));

        // Run job
        remessaWorker.enviarRemessas();

        // Clear current tenant and user
        UserContext.clear();
        TenantContext.clear();
    }
}
