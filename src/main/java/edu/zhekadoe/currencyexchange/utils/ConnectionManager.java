package edu.zhekadoe.currencyexchange.utils;

import lombok.experimental.UtilityClass;

import java.sql.*;

@UtilityClass
public class ConnectionManager {
    //export DB_URL=jdbc:sqlite:/root/apache-tomcat-11.0.2/webapps/CurrencyExchange/WEB-INF/classes/currency_exchanger.sqlite
    private static final String INIT_QUERY = """
            PRAGMA foreign_keys = ON;
            
            CREATE TABLE IF NOT EXISTS currencies(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                full_name   VARCHAR(128) NOT NULL,
                code        VARCHAR(3) NOT NULL UNIQUE,
                sign        VARCHAR(3) NOT NULL);
            
            CREATE TABLE IF NOT EXISTS exchange_rates(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                base_currency_id INT NOT NULL,
                target_currency_id INT NOT NULL,
                rate DECIMAL(9, 6) NOT NULL,
                UNIQUE (base_currency_id, target_currency_id),
                FOREIGN KEY (base_currency_id) REFERENCES currencies(id),
                FOREIGN KEY (target_currency_id) REFERENCES currencies(id));
            """;

    static {
        loadDriver();
        initForeignKeysAndTables();
    }

    //todo ConnectionPool when I learn ReflectionAPI
    public Connection get() {
        try {
            String url = System.getenv("DB_URL");
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void initForeignKeysAndTables() {
        try (Connection conn = get();
             var stmt = conn.createStatement()) {
            stmt.executeUpdate(INIT_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
