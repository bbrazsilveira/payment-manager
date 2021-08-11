package br.com.bbrazsilveira.payment.v1.development;

import org.hibernate.cfg.AvailableSettings;

import javax.persistence.Persistence;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


public class GenerateDDL {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) throws IOException {
        // Create new schema file
        String filename = new SimpleDateFormat("'schema-'yyyyMMddHHmmss'.sql'").format(new Date());
        File file = new File("src/main/resources/db/ddl/" + filename);
        file.createNewFile();

        // Create hibernate properties
        final Properties persistenceProperties = new Properties();

        persistenceProperties.setProperty(AvailableSettings.FORMAT_SQL, "false");
        persistenceProperties.setProperty(AvailableSettings.HBM2DDL_AUTO, "none");
        persistenceProperties.setProperty(AvailableSettings.HBM2DDL_DELIMITER, ";");
        persistenceProperties.setProperty(AvailableSettings.HBM2DLL_CREATE_SCHEMAS, "true");
        persistenceProperties.setProperty(AvailableSettings.HBM2DDL_DATABASE_ACTION, "none");
        persistenceProperties.setProperty(AvailableSettings.HBM2DDL_SCRIPTS_ACTION, "create");
        persistenceProperties.setProperty(AvailableSettings.HBM2DDL_CREATE_SOURCE, "metadata");
        persistenceProperties.setProperty(AvailableSettings.NON_CONTEXTUAL_LOB_CREATION, "true");
        persistenceProperties.setProperty(AvailableSettings.HBM2DDL_SCRIPTS_CREATE_TARGET, file.getAbsolutePath());
        persistenceProperties.setProperty(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "br.com.bbrazsilveira.payment.v1.configuration.utils.SnakeCaseNamingStrategy");

        persistenceProperties.setProperty(AvailableSettings.JPA_JDBC_USER, "postgres");
        persistenceProperties.setProperty(AvailableSettings.JPA_JDBC_PASSWORD, "12345");
        persistenceProperties.setProperty(AvailableSettings.JPA_JDBC_DRIVER, "org.postgresql.Driver");
        persistenceProperties.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQL95Dialect");
        persistenceProperties.setProperty(AvailableSettings.JPA_JDBC_URL, "jdbc:postgresql://localhost:5432/issuer");

        // Generate DDL
        Persistence.generateSchema("punit", persistenceProperties);

        // Add default random uuid value to primary keys
        replaceUUIDDefaultValue(file.toPath());

        System.exit(0);
    }

    private static void replaceUUIDDefaultValue(Path path) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll("\\(id uuid not null,", "(id uuid default gen_random_uuid() not null,");
        Files.write(path, content.getBytes(charset));
    }
}
