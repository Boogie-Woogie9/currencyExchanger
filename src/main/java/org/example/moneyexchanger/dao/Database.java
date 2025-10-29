package org.example.moneyexchanger.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//класс для подключения к БД SQLite
public class Database {

    private static HikariDataSource dataSource;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e){
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }
    /**
     * Иницициализация пула соединений. Вызывается один раз при запуске приложения.
     */

    public static void init(ServletContext context){
        if (dataSource != null ) return;

        String dbPath = context.getRealPath("/WEB-INF/db/database.db");
        String JdbcUrl = "jdbc:sqlite:" + dbPath;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(JdbcUrl);
        config.setDriverClassName("org.sqlite.JDBC");

        // Для SQLite лучше держать небольшой пул
        config.setMaximumPoolSize(5);
        config.setPoolName("SQLitePool");

        // Тестовое подключение, чтобы убедиться, что всё работает
        config.setConnectionTestQuery("SELECT 1");

        // SQLite не поддерживает авто-коммит по умолчанию, но Hikari требует определённости
        config.setAutoCommit(true);

        // Важно: SQLite любит WAL-режим при многопоточности
        config.addDataSourceProperty("journal_mode", "WAL");

        dataSource = new HikariDataSource(config);
    }

    /**
     * Получить соединение из пула.
     */
        public static Connection getConnection() throws SQLException {
        if (dataSource == null)
            throw new IllegalStateException("Database not initialized");
        return dataSource.getConnection();
    }

    /**
     * Закрыть пул соединений (например, при остановке приложения).
     */
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}
