package org.example.moneyexchanger.dao;

import org.example.moneyexchanger.model.Currency;
import org.example.moneyexchanger.model.ExchangeRate;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao implements CrudRepository<ExchangeRate> {

    private final CurrencyDao currencyDao = new CurrencyDao();

    private ExchangeRate createNewExchangeRate(ResultSet resultSet) {
        try {
            return new ExchangeRate(
                    resultSet.getLong(0),
                    currencyDao.findById(resultSet.getLong(1)).get(),
                    currencyDao.findById(resultSet.getLong(2)).get(),
                    resultSet.getBigDecimal(4));
        } catch (SQLException e) {
            return null;
        }
    }

    //CREATE (добавить новые обменные курсы)
    @Override
    public void save(ExchangeRate entity) {

        String sql = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, entity.getBaseCurrency().getId());
            pstmt.setLong(2, entity.getTargetCurrency().getId());
            pstmt.setBigDecimal(3, entity.getRate());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //READ (все обменные курсы)
    @Override
    public List<ExchangeRate> findAll() {
        String sql = "SELECT * FROM ExchangeRates";
        List<ExchangeRate> rates = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)) {

            while (resultSet.next()) {
                rates.add(createNewExchangeRate(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rates;
    }

    //READ (найти куср обмена по id)
    @Override
    public Optional<ExchangeRate> findById(Long id) {
        String sql = "SELECT 1 FROM ExchangeRates WHERE ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery(sql);

            if (rs.wasNull()) {
                System.out.println("ExchangeRate with id = " + id + "was not found");
                return Optional.empty();
            }

            //Получаем данные
            Optional<Currency> base = currencyDao.findById(rs.getLong(1));
            Optional<Currency> target = currencyDao.findById(rs.getLong(2));
            BigDecimal rate = rs.getBigDecimal(3);

            return Optional.ofNullable(createNewExchangeRate(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //READ (найти курс обмена по кодам)
    public Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode) {
        ExchangeRate exchangeRate = null;
        final String query = "SELECT * FROM ExchangeRates WHERE " +
                "basecurrencyid=? AND targetcurrencyid=?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1,
                    currencyDao.findByName(baseCurrencyCode).get().getId());
            statement.setLong(2,
                    currencyDao.findByName(targetCurrencyCode).get().getId());

            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            if (resultSet.next()) {
                exchangeRate = createNewExchangeRate(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(exchangeRate);
    }

    //UPDATE (обновить обменный курс)
    @Override
    public void update(ExchangeRate entity) {
        String sql = "UPDATE ExchangeRates SET Rate = ? WHERE ID = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, entity.getRate());
            pstmt.setLong(2, entity.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("NOTHING TO UPDATE");
            }
            System.out.println("ExchangeRate with id = " + entity.getId() + " was UPDATED successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //DELETE (удалить обменный курс)
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
            System.err.println("Error deleting ExchangeRate with id = " + id + ": " + e.getMessage());

        }
    }
}

