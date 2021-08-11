package br.com.bbrazsilveira.payment.v1.configuration.multitenancy;

import lombok.NonNull;
import lombok.extern.java.Log;
import org.keycloak.adapters.OIDCHttpFacade;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

@Log
public class TenantContext {

    private static ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(@NonNull String tenant) {
        currentTenant.set(tenant.toLowerCase());
    }

    @NonNull
    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }

    private static String getTenantByRequest(String host, String referer) {
        // Referer has higher priority (required for Swagger and CORS)
        if (referer != null) {
            try {
                host = new URI(referer).getHost();
            } catch (URISyntaxException e) {
                log.severe(e.getMessage());
            }
        }
        return host.split("\\.")[0].toLowerCase();
    }

    public static String getTenantByRequest(HttpServletRequest request) {
        return getTenantByRequest(request.getHeader("Host"), request.getHeader("Referer"));
    }

    public static String getTenantByRequest(OIDCHttpFacade.Request request) {
        return getTenantByRequest(request.getHeader("Host"), request.getHeader("Referer"));
    }
}
