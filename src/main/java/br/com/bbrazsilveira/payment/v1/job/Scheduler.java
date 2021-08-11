package br.com.bbrazsilveira.payment.v1.job;

import br.com.bbrazsilveira.payment.v1.configuration.multitenancy.TenantResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Service
public class Scheduler {

    @Autowired
    private DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ProxyScheduler proxyScheduler;

    @Scheduled(cron = "0 * * * * *")
    public void enviarRemessas() {
        try {
            // Find all tenant's schema
            List<String> schemas = getTenantSchemas();

            // Run job for each tenant async
            schemas.forEach(schema -> proxyScheduler.enviarRemessasAsync(TenantResolver.getTenantFromSchema(schema)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getTenantSchemas() throws SQLException {
        List<String> schemas = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        ResultSet resultSet = connection.getMetaData().getSchemas();

        // Find all tenant schemas by prefix excluding default schema
        while (resultSet.next()) {
            String schema = resultSet.getString(1);

            if (TenantResolver.isTenantSchema(schema) && !schema.equals(TenantResolver.getDefaultSchema())) {
                schemas.add(schema);
            }
        }

        // Close connection
        resultSet.close();
        connection.close();
        return schemas;
    }
}
