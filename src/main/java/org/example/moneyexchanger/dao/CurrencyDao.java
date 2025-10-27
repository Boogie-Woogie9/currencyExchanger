package org.example.moneyexchanger.dao;

import java.sql.*;
import java.util.ArrayList;
import org.example.moneyexchanger.model.Currency;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements CrudRepository<Currency> {

    //CREATE (добавить новую валюту)
    @Override
    public void save(Currency entity) {
        String sql = "INSERT INTO Currencies (name, code, sign) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, entity.getName());
            pstmt.setString(2, entity.getCode());
            pstmt.setString(3, entity.getSign());
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                entity.setId(keys.getLong(1));
            } else {
                throw new SQLException("Currency insert failed, no ID obtained");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //READ (все валюты)
    @Override
    public List<Currency> findAll() {
        String request = "SELECT * FROM Currencies";
        List<Currency> currencies = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(request)){

            while (rs.next()){
                Currency currency = new Currency(
                        rs.getLong(0),
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3)
                );
                currencies.add(currency);
                System.out.println("Currency Found: " + currency);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currencies;
    }

    //READ (найти валюту по id)
    @Override
    public Optional<Currency> findById(Long id) {
        String sql = "SELECT * FROM Currencies WHERE ID = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, String.valueOf(id));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Currency(
                        rs.getLong(0),
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3)
                ));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    //READ (найти валюту по имени)
    public Optional<Currency> findByName(String name) {
        String query = "SELECT * FROM Currencies WHERE name = ?";
        try (Connection conn = Database.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);

            pstmt.executeQuery();
            ResultSet resultSet = pstmt.getResultSet();

            if (resultSet.next()){
                return Optional.of(new Currency(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4)
                ));
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //READ (найти валюту по коду)
    public Optional<Currency> findByCode(String code) {
        String query = "SELECT * FROM Currencies WHERE code = ?";

        try (Connection conn = Database.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, code);

            pstmt.executeQuery();
            ResultSet resultSet = pstmt.getResultSet();

            if (resultSet.next()){
                return Optional.of(new Currency(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4)
                ));
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //UPDATE (обновить валюту)
    @Override
    public void update(Currency entity) {
        String sql = "UPDATE Currencies SET Code = ?, FullName = ?, Sign = ? WHERE ID = ? ";

        try (Connection conn = Database.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, entity.getCode());
            pstmt.setString(2, entity.getName());
            pstmt.setString(3, entity.getSign());
            pstmt.setLong(4, entity.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("NOTHING TO UPDATE");
            } else {
                System.out.println("UPDATED SUCCESSFULLY");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //DELETE (удалить валюту)
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM Currencies WHERE ID = ?";
        try (Connection conn = Database.getConnection()){
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0){
                System.out.println("Currency with id = " + id + " deleted successfully.");
            } else {
                System.out.println("Currency with id = " + id + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
