package org.example.moneyexchanger.service;


import org.example.moneyexchanger.dao.CurrencyDao;
import org.example.moneyexchanger.model.Currency;
import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    private final CurrencyDao currencyDao = new CurrencyDao();

    public List<Currency> getAllCurrencies() throws SQLException {
        return currencyDao.getAllCurrencies();
    }

    public Currency getCurrencyById(int id) throws SQLException {
        Currency currency = currencyDao.getCurrencyById(id);

        if (currency == null){
            throw new IllegalArgumentException("Currency with ID = " + id + " not found");
        }
        return currency;
    }

    public void createCurrency(String code, String name, String sign) throws SQLException {
        if (code == null || code.isBlank()){
            throw new IllegalArgumentException("Currency code is not valid!");
        }
        if (sign == null || sign.isBlank()){
            throw new IllegalArgumentException("Currency sign is not valid!");
        }
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Currency name is not valid!");
        }

        Currency newCurrency = new Currency(0, code, name, sign);
        currencyDao.insertCurrency(newCurrency);
    }

    public void updateCurrency(int id, String code, String name, String sign) throws SQLException {
        Currency existing = currencyDao.getCurrencyById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Cannot update - updatedCurrency does not exist!");
        }
        if (code == null || code.isBlank()){
            throw new IllegalArgumentException("Currency code is not valid!");
        }
        if (sign == null || sign.isBlank()){
            throw new IllegalArgumentException("Currency sign is not valid!");
        }
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Currency name is not valid!");
        }
        // Можно добавить бизнес-логику: например, проверку формата кода
        if (!code.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("Invalid updatedCurrency code format (must be 3 uppercase letters)");
        }
        Currency updatingCurrency = new Currency(id, code, name, sign);
        currencyDao.updateCurrency(updatingCurrency);
    }

    public void deleteCurrencyById(int id) throws SQLException {
        Currency existing = currencyDao.getCurrencyById(id);

        if (existing == null) {
            throw new IllegalArgumentException("Cannot delete currency - currency doesn't exist!");
        }
        currencyDao.deleteCurrencyById(id);
    }

}
