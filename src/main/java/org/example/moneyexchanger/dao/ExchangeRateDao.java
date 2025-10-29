package org.example.moneyexchanger.dao;

import org.example.moneyexchanger.model.Currency;
import org.example.moneyexchanger.model.ExchangeRate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.*;

public class ExchangeRateDao implements CrudRepository<ExchangeRate> {

    private final CurrencyDao currencyDao = new CurrencyDao();

    private ExchangeRate mapResultSetToExchangeRate(ResultSet rs, Map<Long, Currency> currencyCache) throws SQLException {
        long baseId = rs.getLong("BaseCurrencyId");
        long targetId = rs.getLong("TargetCurrencyId");

        Currency baseCurrency = currencyCache.computeIfAbsent(baseId, id ->
                currencyDao.findById(id).orElse(null)
        );

        Currency targetCurrency = currencyCache.computeIfAbsent(targetId, id ->
                currencyDao.findById(id).orElse(null)
        );

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

    // READ all
    @Override
    public List<ExchangeRate> findAll() {
        String sql = "SELECT ID, BaseCurrencyId, TargetCurrencyId, Rate FROM ExchangeRates";
        List<ExchangeRate> rates = new ArrayList<>();
        Map<Long, Currency> cache = new HashMap<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                rates.add(mapResultSetToExchangeRate(rs, cache));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load ExchangeRates", e);
        }

        return rates;
    }

    // READ by id
    @Override
    public Optional<ExchangeRate> findById(Long id) {
        String sql = "SELECT ID, BaseCurrencyId, TargetCurrencyId, Rate FROM ExchangeRates WHERE ID = ?";
        Map<Long, Currency> cache = new HashMap<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToExchangeRate(rs, cache));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find ExchangeRate by id: " + id, e);
        }

        return Optional.empty();
    }

    // READ by codes
    public Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode) {
        try {
            Currency base = currencyDao.findByCode(baseCurrencyCode)
                    .orElseThrow(() -> new SQLException("Base currency not found: " + baseCurrencyCode));

            Currency target = currencyDao.findByCode(targetCurrencyCode)
                    .orElseThrow(() -> new SQLException("Target currency not found: " + targetCurrencyCode));

            String sql = "SELECT ID, BaseCurrencyId, TargetCurrencyId, Rate FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";

            try (Connection conn = Database.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setLong(1, base.getId());
                pstmt.setLong(2, target.getId());

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Map<Long, Currency> cache = Map.of(
                                base.getId(), base,
                                target.getId(), target
                        );
                        return Optional.of(mapResultSetToExchangeRate(rs, new HashMap<>(cache)));
                    }
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
                System.out.println("ExchangeRate with id = " + entity.getId() + " not found for update.");
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
                System.out.println("ExchangeRate with id = " + id + " not found for deletion.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete ExchangeRate", e);
        }
    }
}



