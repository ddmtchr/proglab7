package database;

import server.Server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnector {
    private final Logger logger = Logger.getLogger(DBConnector.class.getName());
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
//            e.printStackTrace();
            testConnection = null;
            Server.shutdown();
        }
        this.connection = testConnection;
    }

    public Connection getConnection() {
        try {
            if (!connection.isClosed()) return this.connection;
            else return DriverManager.getConnection(jdbcURL, properties);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Ошибка подключения к БД");
//            e.printStackTrace();
            Server.shutdown();
        }
        return null;
    }

    public <T> T handleQuery(SQLFunction<Connection, T> query) throws SQLException {
        try (Connection connection = DriverManager.getConnection(jdbcURL, properties)) {
            return query.apply(connection);
        }
    }

    public void close() {
        try {
            if (!connection.isClosed()) connection.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Ошибка закрытия соединения");
            e.printStackTrace();
        }
    }
}
