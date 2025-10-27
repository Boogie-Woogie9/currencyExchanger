package org.example.moneyexchanger.dao;

import org.example.moneyexchanger.model.Currency;
import org.example.moneyexchanger.model.ExchangeRate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao implements CrudRepository<ExchangeRate> {

    private final CurrencyDao currencyDao = new CurrencyDao();

    private ExchangeRate createNewExchangeRate(ResultSet rs) throws SQLException {
        Currency baseCurrency = currencyDao.findById(rs.getLong("BaseCurrencyId")).orElse(null);
        Currency targetCurrency = currencyDao.findById(rs.getLong("TargetCurrencyId")).orElse(null);

        return new ExchangeRate(
                rs.getLong("ID"),
                baseCurrency,
                targetCurrency,
                rs.getBigDecimal("Rate")
        );
    }

    // CREATE
    @Override
    public void save(ExchangeRate entity) {
        String sql = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, entity.getBaseCurrency().getId());
            pstmt.setLong(2, entity.getTargetCurrency().getId());
            pstmt.setBigDecimal(3, entity.getRate());
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save ExchangeRate", e);
        }
    }

    // READ (все)
    @Override
    public List<ExchangeRate> findAll() {
        String sql = "SELECT ID, BaseCurrencyId, TargetCurrencyId, Rate FROM ExchangeRates";
        List<ExchangeRate> rates = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                rates.add(createNewExchangeRate(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load ExchangeRates", e);
        }

        return rates;
    }

    // READ (по ID)
    @Override
    public Optional<ExchangeRate> findById(Long id) {
        String sql = "SELECT ID, BaseCurrencyId, TargetCurrencyId, Rate FROM ExchangeRates WHERE ID = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createNewExchangeRate(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find ExchangeRate by id: " + id, e);
        }

        return Optional.empty();
    }

    // READ (по кодам валют)
    public Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode) {
        String sql = "SELECT ID, BaseCurrencyId, TargetCurrencyId, Rate FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            Long baseId = currencyDao.findByCode(baseCurrencyCode)
                    .orElseThrow(() -> new SQLException("Base currency not found: " + baseCurrencyCode))
                    .getId();

            Long targetId = currencyDao.findByCode(targetCurrencyCode)
                    .orElseThrow(() -> new SQLException("Target currency not found: " + targetCurrencyCode))
                    .getId();

            pstmt.setLong(1, baseId);
            pstmt.setLong(2, targetId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createNewExchangeRate(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find ExchangeRate by codes", e);
        }

        return Optional.empty();
    }

    // UPDATE
    @Override
    public void update(ExchangeRate entity) {
        String sql = "UPDATE ExchangeRates SET Rate = ?, BaseCurrencyId = ?, TargetCurrencyId = ? WHERE ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, entity.getRate());
            pstmt.setLong(2, entity.getBaseCurrency().getId());
            pstmt.setLong(3, entity.getTargetCurrency().getId());
            pstmt.setLong(4, entity.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("NOTHING TO UPDATE");
            } else {
                System.out.println("ExchangeRate with id = " + entity.getId() + " UPDATED successfully");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update ExchangeRate", e);
        }
    }

    // DELETE
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM ExchangeRates WHERE ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("ExchangeRate with id = " + id + " not found.");
            } else {
                System.out.println("ExchangeRate with id = " + id + " was deleted.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete ExchangeRate", e);
        }
    }
}


