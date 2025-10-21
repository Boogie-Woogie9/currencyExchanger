package org.example.moneyexchanger.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//класс для подключения к БД SQLite
public class Database {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/database.db";  //url к файлу с БД

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException exception){
            throw new RuntimeException("SQLite JDBC driver not found", exception);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
