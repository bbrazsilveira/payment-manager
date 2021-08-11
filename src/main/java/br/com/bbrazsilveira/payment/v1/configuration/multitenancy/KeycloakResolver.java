package br.com.bbrazsilveira.payment.v1.configuration.multitenancy;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.OIDCHttpFacade;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.beans.factory.annotation.Value;

public class KeycloakResolver implements KeycloakConfigResolver {

    @Value("${app.keycloak.realm}")
    private String realm;

    @Value("${app.keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${app.keycloak.resource}")
    private String resource;

    @Value("${app.keycloak.principal-attribute}")
    private String principalAttribute;

    @Override
    public KeycloakDeployment resolve(OIDCHttpFacade.Request request) {
        String tenant = TenantContext.getTenantByRequest(request);
        String resourceWithTenant = String.format("%s_%s", resource , tenant);

        AdapterConfig config = new AdapterConfig();
        config.setRealm(realm);
        config.setAuthServerUrl(authServerUrl);
        config.setPrincipalAttribute(principalAttribute);
        config.setUseResourceRoleMappings(true);
        config.setBearerOnly(true);
        config.setResource(resourceWithTenant);

        return KeycloakDeploymentBuilder.build(config);
    }
}