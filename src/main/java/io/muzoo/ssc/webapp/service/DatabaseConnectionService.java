package io.muzoo.ssc.webapp.service;

import com.zaxxer.hikari.HikariDataSource;
import io.muzoo.ssc.webapp.config.ConfigProperties;
import io.muzoo.ssc.webapp.config.ConfigurationLoader;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * We'llmake this singleton too
 */
public class DatabaseConnectionService {

    private final HikariDataSource ds;

    private static DatabaseConnectionService service;

    /**
     * Database connection pool using hikari library
     * The secret and variables are loaded from disk
     * the file config.properties is not committed to git repo (maybe lol)
     */

    private DatabaseConnectionService() {
        ds = new HikariDataSource();
        ds.setMaximumPoolSize(20);
        ConfigProperties configProperties = ConfigurationLoader.load();
        if (configProperties == null) {
            throw new RuntimeException("Unable to read the config.properties");
        }
        ds.setDriverClassName(configProperties.getDatabaseDriverClassName());
        ds.setJdbcUrl(configProperties.getDatabaseConnectionUrl());
        ds.addDataSourceProperty("user", configProperties.getDatabaseUsername());
        ds.addDataSourceProperty("password", configProperties.getDatabasePassword());
        ds.setAutoCommit(false);
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static DatabaseConnectionService getInstance() {
        if (service == null) {
            service = new DatabaseConnectionService();
        }
        return service;
    }
}
