package org.example.moneyexchanger.dao;

import java.sql.*;
import java.util.ArrayList;
import org.example.moneyexchanger.model.Currency;
import java.util.List;

public class CurrencyDao {

    //READ (все валюты)
    public List<Currency> getAllCurrencies() throws SQLException {
        String request = "SELECT * FROM Currencies";
        List<Currency> currencies = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(request)){

            while (rs.next()){
                Currency currency = new Currency(
                        rs.getInt(0),
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3)
                );
                currencies.add(currency);
                System.out.println("Currency Found: " + currency);
            }
        }
        return currencies;
    }

    //READ (найти валюту по id)
    public Currency getCurrencyById(int id) throws SQLException {
        String sql = "SELECT * FROM Currencies WHERE ID = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, String.valueOf(id));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Currency(
                        rs.getInt(0),
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3)
                );
            } else {
                return null;
            }
        }
    }

    //READ (найти валюту по коду)
    public Currency getCurrencyByCode(String code) throws SQLException {
        String sql = "SELECT * FROM Currencies WHERE code = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Currency(
                        rs.getInt(0),
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3)
                );
            } else {
                return null;
            }
        }
    }

    //CREATE (добавить новую валюту)
    public Currency insertCurrency(String name, String code, String sign) throws SQLException {
        String sql = "INSERT INTO Currencies (name, code, sign) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setString(2, code);
            pstmt.setString(3, sign);
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                return new Currency(keys.getInt(1), name, code, sign);
            } else {
                throw new SQLException("Currency insert failed, no ID obtained");
            }
        }
    }

    //DELETE (удалить валюту)
    public void deleteCurrencyById(int id) throws SQLException {
        String sql = "DELETE FROM Currencies WHERE ID = ?";
        try (Connection conn = Database.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0){
                System.out.println("Currency with id = " + id + " deleted successfully.");
            } else {
                System.out.println("Currency with id = " + id + " not found.");
            }
        }
    }

    //UPDATE (обновить валюту)
    public Currency updateCurrency(int id, String newCode, String newName, String newSign) throws SQLException {
        String sql = "UPDATE Currencies SET Code = ?, FullName = ?, Sign = ? WHERE ID = ? ";

        try (Connection conn = Database.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newCode);
            pstmt.setString(2, newName);
            pstmt.setString(3, newSign);
            pstmt.setInt(4, id);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("NOTHING TO UPDATE");
                return null;
            }
            System.out.println("UPDATED SUCCESSFULLY");
            return getCurrencyById(id);
        }
    }
}
