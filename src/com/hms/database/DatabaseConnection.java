package com.hms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection manager.
 * Uses Singleton pattern to ensure only one connection pool entry point.
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/hms_cli_db"
                                         + "?useSSL=false&serverTimezone=Asia/Karachi"
                                         + "&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = "";          // XAMPP default: empty

    private static DatabaseConnection instance;
    private Connection connection;

    // Private constructor: Singleton pattern
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "MySQL JDBC Driver not found. Add mysql-connector-j to /lib.", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                "Cannot connect to MariaDB. Is XAMPP running?\n" + e.getMessage(), e);
        }
    }

    /**
     * Returns the singleton instance, creating it if necessary.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Returns the active Connection, reconnecting if closed.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            instance = new DatabaseConnection();
        }
        return connection;
    }

    /**
     * Closes the connection. Call only on application shutdown.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
