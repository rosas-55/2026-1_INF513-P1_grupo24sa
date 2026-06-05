package com.tecnoweb.grupo24sa.ConfigDB;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConection {
    private static final String DRIVER = "jdbc:postgresql://";
    
    // Pool estático: una sola piscina para toda la aplicación
    private static HikariDataSource dataSource; 

    // Conexión instanciada: cada objeto de DB usa temporalmente su propia conexión
    private Connection currentConnection; 

    private String user;
    private String password;
    private String host;
    private String port;
    private String db_name;
    private String url;

    public DatabaseConection(String user, String password, String host, String port, String db_name) {
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
        this.db_name = db_name;
        this.url = DRIVER + host + ":" + port + "/" + db_name; 

        // Inicializar el pool solo la primera vez que se instancia DatabaseConection
        if (dataSource == null) {
            initPool();
        }
    }

    private synchronized void initPool() {
        if (dataSource == null) {
            try {
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(this.url);
                config.setUsername(this.user);
                config.setPassword(this.password);
                
                // Configuración óptima del pool
                config.setMaximumPoolSize(20); // Máximo 20 conexiones simultáneas
                config.setMinimumIdle(5);      // Mantiene 5 conexiones listas
                config.setConnectionTimeout(30000); // 30 segundos de espera máximo

                dataSource = new HikariDataSource(config);
                System.out.println("HikariCP Connection Pool inicializado correctamente.");
            } catch (Exception e) {
                System.err.println("Error inicializando HikariCP: " + e.getMessage());
            }
        }
    }

    public Connection openConnection() {
        try {
            // Si no tenemos una conexión activa en ESTA instancia, pedimos una al pool
            if (currentConnection == null || currentConnection.isClosed()) {
                currentConnection = dataSource.getConnection();
            }
        } catch (SQLException ex) {
            System.err.println("Error al obtener conexion del pool: " + ex.getMessage());
            ex.printStackTrace();
        }
        return currentConnection;
    }

    public void closeConnection() {
        try {
            // Al llamar a close() en una conexión de Hikari, 
            // no se destruye, simplemente se "devuelve" al pool para que otro proceso la use.
            if (currentConnection != null && !currentConnection.isClosed()) {
                currentConnection.close();
                currentConnection = null;
            }
        } catch (SQLException ex) {
            System.err.print("Error al devolver la conexion al pool: " + ex.getMessage());
        }
    }
}
