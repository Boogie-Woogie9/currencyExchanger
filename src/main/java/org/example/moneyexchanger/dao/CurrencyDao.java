package org.example.moneyexchanger.dao;

import java.sql.*;
import java.util.ArrayList;
import org.example.moneyexchanger.model.Currency;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements CrudRepository<Currency> {


    // OPTIMIZE
    private Optional<Currency> findByField(String field, String value) {
        String sql = "SELECT ID, Code, FullName, Sign FROM Currencies WHERE " + field + " = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Currency(
                            rs.getLong("ID"),
                            rs.getString("FullName"),
                            rs.getString("Code"),
                            rs.getString("Sign")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    // CREATE
    @Override
    public void save(Currency entity) {
        String sql = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, entity.getCode());
            pstmt.setString(2, entity.getName());
            pstmt.setString(3, entity.getSign());
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getLong(1));
                } else {
                    throw new SQLException("Currency insert failed, no ID obtained");
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                throw new RuntimeException("Currency with code " + entity.getCode() + " already exists", e);
            }
            throw new RuntimeException(e);
        }
    }

    // READ (все валюты)
    @Override
    public List<Currency> findAll() {
        String sql = "SELECT ID, Code, FullName, Sign FROM Currencies";
        List<Currency> currencies = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Currency currency = new Currency(
                        rs.getLong("ID"),
                        rs.getString("FullName"),
                        rs.getString("Code"),
                        rs.getString("Sign")
                );
                currencies.add(currency);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currencies;
    }

    // READ (по ID)
    @Override
    public Optional<Currency> findById(Long id) { return findByField("ID", id.toString()); }

    // READ (по имени)
    public Optional<Currency> findByName(String fullName) { return findByField("FullName", fullName); }

    // READ (по коду)
    public Optional<Currency> findByCode(String code) { return findByField("Code", code); }

    // UPDATE
    @Override
    public void update(Currency entity) {
        String sql = "UPDATE Currencies SET Code = ?, FullName = ?, Sign = ? WHERE ID = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entity.getCode());
            pstmt.setString(2, entity.getName());
            pstmt.setString(3, entity.getSign());
            pstmt.setLong(4, entity.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("NOTHING TO UPDATE");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DELETE
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM Currencies WHERE ID = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Currency with id = " + id + " deleted successfully.");
            } else {
                System.out.println("Currency with id = " + id + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

