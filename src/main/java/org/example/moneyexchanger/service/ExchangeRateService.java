package org.example.moneyexchanger.service;

import org.example.moneyexchanger.dao.CurrencyDao;
import org.example.moneyexchanger.dao.ExchangeRateDao;
import org.example.moneyexchanger.model.Currency;
import org.example.moneyexchanger.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ExchangeRateService {
    public final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    public final CurrencyDao currencyDao = new CurrencyDao();

    public List<ExchangeRate> getAllExchangeRates() throws SQLException {
        return exchangeRateDao.getAllExchangeRates() ;
    }

//    public ExchangeRate getExchangeRateByCodes()

    public void createExchangeRate(int baseId, int targetId, BigDecimal rate) throws SQLException {
        Currency base = currencyDao.getCurrencyById(baseId);
        Currency target = currencyDao.getCurrencyById(targetId);

        if (base == null || target == null){
            throw new IllegalArgumentException("Selected currncies don't exist.");
        }
        if (rate.doubleValue() <= 0 ) {
            throw new IllegalArgumentException("Exchange rate must be positive.");
        }

        ExchangeRate newExchangeRate = new ExchangeRate(0, base, target, rate);
        exchangeRateDao.
    }
}
