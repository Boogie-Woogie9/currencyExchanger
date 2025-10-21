package org.example.moneyexchanger.dao;

import org.example.moneyexchanger.model.Currency;
import org.example.moneyexchanger.model.ExchangeRate;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao {
    private final CurrencyDao currencyDao = new CurrencyDao();

    //READ (все обменные курсы)
    public List<ExchangeRate> getAllExchangeRates() throws SQLException {
        String sql = "SELECT * FROM ExchangeRates";
        List<ExchangeRate> rates = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Currency base = currencyDao.getCurrencyById(rs.getInt(1));
                Currency target = currencyDao.getCurrencyById(rs.getInt(2));
                BigDecimal rate = rs.getBigDecimal(3);

                rates.add(new ExchangeRate(
                        rs.getInt(0),
                        base,
                        target,
                        rate
                ));
            }
        }
        return rates;
    }

    //CREATE (добавить новые обменные курсы)
    public ExchangeRate insertExchangeRate(String baseCode, String targetCode, BigDecimal rateValue) throws SQLException {
        Currency base = currencyDao.getCurrencyByCode(baseCode);
        Currency target = currencyDao.getCurrencyByCode(targetCode);

        if (base == null || target == null)
            throw new SQLException("One or both currencies not found");

        String sql = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, base.getId());
            pstmt.setInt(2, target.getId());
            pstmt.setBigDecimal(3, rateValue);
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                return new ExchangeRate(keys.getInt(1), base, target, rateValue);
            } else {
                throw new SQLException("ExchangeRate insert failed, no ID obtained");
            }
        }
    }

    //DELETE (удалить обменный курс)
    public void deleteExchangeRateById(int id) throws SQLException {
        String sql = "DELETE FROM ExchangeRates WHERE ID = ?";

        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("ExchangeRate with id = " + id + " not found.");
            } else {
                System.out.println("ExchangeRate with id = " + id + " was deleted.");
            }

        } catch (SQLException e) {
            System.err.println("Error deleting ExchangeRate with id = " + id + ": " + e.getMessage());

        }
    }

    //UPDATE (обновить обменный курс - только сам курс)
    public ExchangeRate updateExchangeRate(int id, BigDecimal newRate) throws SQLException{
        String sql = "UPDATE ExchangeRates SET Rate = ? WHERE ID = ?";
        
        try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newRate);
            pstmt.setInt(2, id);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0){
                System.out.println("NOTHING TO UPDATE");
            }
            System.out.println("ExchangeRate with id = " + id + " was UPDATED successfully");
        }
        return getExchangeRateById(id);
    }

    //READ (найти обменный курс по id)
    private ExchangeRate getExchangeRateById(int id) throws SQLException{
        String sql = "SELECT 1 FROM ExchangeRates WHERE ID = ?";

        try (Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery(sql);

            if (rs.wasNull()){
                System.out.println("ExchangeRate with id = " + id + "was not found");
                return null;
            }

            //Получаем данные
            Currency base = currencyDao.getCurrencyById(rs.getInt(1));
            Currency target = currencyDao.getCurrencyById(rs.getInt(2));
            BigDecimal rate = rs.getBigDecimal(3);

            return new ExchangeRate(
                    rs.getInt(0),
                    base,
                    target,
                    rate
            );
        }
    }
}

