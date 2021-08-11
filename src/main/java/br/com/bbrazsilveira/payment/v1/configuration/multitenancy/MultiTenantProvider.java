package br.com.bbrazsilveira.payment.v1.configuration.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


@Component
public class MultiTenantProvider implements MultiTenantConnectionProvider {

    @Autowired
    private DataSource dataSource;

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenant) throws SQLException {
        final Connection connection = getAnyConnection();
        connection.setSchema(TenantResolver.getTenantSchema(tenant));
        return connection;

    }

    @Override
    public void releaseConnection(String tenant, Connection connection) throws SQLException {
        connection.setSchema(TenantResolver.getDefaultSchema());
        releaseAnyConnection(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;

    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
}