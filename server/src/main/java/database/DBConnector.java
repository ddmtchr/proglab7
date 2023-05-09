package database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {
    private final String jdbcURL = "jdbc:postgresql://localhost:5432/studs";
    private final Properties properties = new Properties();
    private final Connection connection;

    public DBConnector() {
        Connection testConnection;
        try {
            ClassLoader loader = DBConnector.class.getClassLoader();
            properties.load(loader.getResourceAsStream("config/cfg.properties"));
            testConnection = DriverManager.getConnection(jdbcURL, properties);
        } catch (SQLException | IOException e) {
            System.out.println("Ошибка подключения к БД");
            e.printStackTrace();
            testConnection = null;
            System.exit(0);
        }
        this.connection = testConnection;
    }

    public Connection getConnection() {
        try {
            if (!connection.isClosed()) return this.connection;
            else return DriverManager.getConnection(jdbcURL, properties);
        } catch (SQLException e) {
            System.out.println("Ошибка подключения к БД");
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
}
