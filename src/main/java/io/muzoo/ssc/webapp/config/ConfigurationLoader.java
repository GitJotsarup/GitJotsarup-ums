package io.muzoo.ssc.webapp.config;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfigurationLoader {
    // here we have now added a static method for loading configurations from a disk
    // default location is 'config.properties' in the same folder
    public static ConfigProperties load() {
        String configFileName = "config.properties";
        try(FileInputStream fin = new FileInputStream(configFileName)) {
            Properties prop = new Properties();

            prop.load(fin);

            // get the property value and print it out
            String driverClassName = prop.getProperty("database.driverClassName");
            String connectionUrl = prop.getProperty("database.connectionUrl");
            String username = prop.getProperty("database.username");
            String password = prop.getProperty("database.password");

            return new ConfigProperties.ConfigPropertiesBuilder().
                    databaseDriverClassName(driverClassName).
                    databaseConnectionUrl(connectionUrl).
                    databaseUsername(username).
                    databasePassword(password).
                    build();

        } catch (Exception e) {
            return null;
        }
    }

}
