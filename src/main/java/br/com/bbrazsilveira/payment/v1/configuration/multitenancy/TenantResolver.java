package br.com.bbrazsilveira.payment.v1.configuration.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class TenantResolver implements CurrentTenantIdentifierResolver {

    private static final String PREFIX_TENANT = "tn_";
    private static final String DEFAULT_TENANT = "default";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getCurrentTenant();
        if (tenant != null) {
            return tenant;
        }
        return DEFAULT_TENANT;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    public static String getTenantFromSchema(String schema) {
        return schema.substring(3).toLowerCase();
    }

    public static boolean isTenantSchema(String schema) {
        return schema.startsWith(PREFIX_TENANT);
    }

    public static String getTenantSchema(String tenant) {
        return PREFIX_TENANT + tenant;
    }

    public static String getDefaultSchema() {
        return PREFIX_TENANT + DEFAULT_TENANT;
    }
}
