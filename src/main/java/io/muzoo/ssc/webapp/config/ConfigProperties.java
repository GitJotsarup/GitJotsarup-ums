package io.muzoo.ssc.webapp.config;

import lombok.*;

@Getter
@Setter
@Builder
public class ConfigProperties {

    private String databaseDriverClassName;
    private String databaseConnectionUrl;
    private String databaseUsername;
    private String databasePassword;


}
