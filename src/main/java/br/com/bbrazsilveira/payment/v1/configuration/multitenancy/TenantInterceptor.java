package br.com.bbrazsilveira.payment.v1.configuration.multitenancy;

import br.com.bbrazsilveira.payment.v1.configuration.security.UserContext;
import br.com.bbrazsilveira.payment.v1.domain.model.conta.User;
import br.com.bbrazsilveira.payment.v1.repository.UserRepository;
import lombok.extern.java.Log;
import org.keycloak.KeycloakPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Log
@Component
public class TenantInterceptor extends HandlerInterceptorAdapter {

    @Value("${app.keycloak.admin-id}")
    private UUID adminId;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            updateCurrentTenant(request);
            updateCurrentUser();
        } catch (Exception e) {
            if (e instanceof DataAccessException) {
                throw new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED, "No tenant found.");
            } else {
                e.printStackTrace();
                log.severe(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        return true;
    }

    private void updateCurrentTenant(HttpServletRequest request) {
        // Get tenant of sub-domain
        String tenant = TenantContext.getTenantByRequest(request);
        TenantContext.setCurrentTenant(tenant);
    }

    private void updateCurrentUser() {
        UUID userId = adminId;

        // Check if user is authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof KeycloakPrincipal) {

            // Find authenticated user
            userId = UUID.fromString(authentication.getName());
        }

        User user = entityManager.getReference(User.class, userId);
        UserContext.setCurrentUser(user);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        TenantContext.clear();
        UserContext.clear();
    }
}