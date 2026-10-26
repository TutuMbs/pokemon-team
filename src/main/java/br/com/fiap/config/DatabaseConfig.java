package br.com.fiap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {
    @Value("${DB_URL:}") private String dbUrl;
    @Value("${DATABASE_URL:}") private String databaseUrl;
    @Value("${DB_USER:sa}") private String dbUser;
    @Value("${DB_PASSWORD:}") private String dbPassword;

    @Bean
    public DataSource dataSource() {
        String configuredUrl = dbUrl.isBlank() ? databaseUrl : dbUrl;
        if (configuredUrl.isBlank()) {
            return DataSourceBuilder.create().url("jdbc:h2:file:./data/pokemon-team;AUTO_SERVER=TRUE").username("sa").password("").build();
        }
        if (configuredUrl.startsWith("postgres://") || configuredUrl.startsWith("postgresql://")) return postgresDataSource(configuredUrl);
        return DataSourceBuilder.create().url(configuredUrl).username(dbUser).password(dbPassword).build();
    }

    private DataSource postgresDataSource(String connectionString) {
        try {
            URI uri = URI.create(connectionString.replaceFirst("^postgres://", "postgresql://"));
            String user = dbUser, password = dbPassword;
            if (uri.getUserInfo() != null) {
                String[] credentials = uri.getUserInfo().split(":", 2);
                user = credentials[0];
                if (credentials.length > 1) password = credentials[1];
            }
            String url = "jdbc:postgresql://" + uri.getHost() + ":" + (uri.getPort() > 0 ? uri.getPort() : 5432) + uri.getPath();
            return DataSourceBuilder.create().url(url).username(user).password(password).build();
        } catch (Exception e) { throw new IllegalArgumentException("DATABASE_URL inválida para PostgreSQL", e); }
    }
}
