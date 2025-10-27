package org.example.moneyexchanger.service;


import org.example.moneyexchanger.dao.CurrencyDao;
import org.example.moneyexchanger.model.Currency;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CurrencyService {

    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    private void validate(String code, String name, String sign) {
        if (code == null || code.isBlank()){
            throw new IllegalArgumentException("Currency code is invalid");
        }
        if (sign == null || sign.isBlank()){
            throw new IllegalArgumentException("Currency sign is invalid");
        }
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Currency name is invalid");
        }
        if (!code.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("Invalid updatedCurrency code format (must be 3 uppercase letters)");
        }
    }

    public List<Currency> getAllCurrencies() {
        return currencyDao.findAll();
    }

    public Currency getCurrencyById(Long id) {
        return currencyDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Currency with id=" + id + " not found"));
    }

    public Currency getCurrencyBuName(String name){
        return currencyDao.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("Currency with name=" + name + " not found"));
    }

    public Currency createCurrency(String code, String name, String sign) {
        validate(code, name, sign);
        Currency newCurrency = new Currency(code, name, sign);
        currencyDao.save(newCurrency);
        return newCurrency;
    }

    public void updateCurrency(Long id, String code, String name, String sign) {
        currencyDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Currency with id=" + id + " not found"));

        validate(code, name, sign);
        currencyDao.update(new Currency(id, code, name, sign));
    }

    public void deleteCurrencyById(Long id) {
        currencyDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Currency with id=" + id + " not found"));

        currencyDao.delete(id);
    }
}
