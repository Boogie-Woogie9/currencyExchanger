package org.example.moneyexchanger.dao;

import jakarta.servlet.ServletContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//класс для подключения к БД SQLite
public class Database {
//    private static String DB_URL = "jdbc:sqlite:src/main/resources/database.db";  //url к файлу с БД
    private static String DB_URL;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e){
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }

    public static void init(ServletContext context){
        String dbPath = context.getRealPath("/WEB-INF/db/database.db");
        DB_URL = "jdbc:sqlite:" + dbPath;
    }

    public static Connection getConnection() throws SQLException {
        if (DB_URL == null) throw new IllegalStateException("Database not initialized");
        return DriverManager.getConnection(DB_URL);
    }
}
