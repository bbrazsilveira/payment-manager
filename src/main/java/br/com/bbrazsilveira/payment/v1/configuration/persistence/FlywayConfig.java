package br.com.bbrazsilveira.payment.v1.configuration.persistence;

import br.com.bbrazsilveira.payment.v1.configuration.multitenancy.TenantResolver;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
public class FlywayConfig {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .schemas(TenantResolver.getDefaultSchema())
                .load();
    }

    @Bean
    public Boolean flywayMultiTenant(DataSource dataSource) throws SQLException {
        ResultSet resultSet = dataSource.getConnection().getMetaData().getSchemas();

        // Find all tenant schemas by prefix excluding default schema
        while (resultSet.next()) {
            String schema = resultSet.getString(1);

            if (TenantResolver.isTenantSchema(schema) && !schema.equals(TenantResolver.getDefaultSchema())) {
                logger.info("Schema '{}' prepared for migration", schema);
                Flyway.configure().locations("db/migration/common", "db/migration/" + schema)
                        .dataSource(dataSource)
                        .schemas(schema)
                        .load()
                        .migrate();
            }
        }

        return true;
    }
}