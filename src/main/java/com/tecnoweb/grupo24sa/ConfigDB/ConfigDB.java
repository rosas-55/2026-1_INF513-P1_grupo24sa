package com.tecnoweb.grupo24sa.ConfigDB;

import com.tecnoweb.grupo24sa.utils.EnvConfig;

public class ConfigDB {
    private final String user;
    private final String password;
    private final String host;
    private final String port;
    private final String dbName;

    public ConfigDB() {
        this.user = "grupo24sa";
        this.password = "grup024grup024*";
        this.host = EnvConfig.get("MAIL_HOST", "mail.tecnoweb.org.bo");
        this.port = "5432";
        this.dbName = "db_grupo24sa";
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDbName() {
        return dbName;
    }
}
