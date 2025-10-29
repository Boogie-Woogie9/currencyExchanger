package org.example.moneyexchanger.service;


import org.example.moneyexchanger.dao.CurrencyDao;
import org.example.moneyexchanger.model.Currency;
import java.util.List;
import java.util.NoSuchElementException;

import static org.example.moneyexchanger.utils.Utils.validate;

public class CurrencyService {

    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
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

    public Currency createCurrency(Currency newCurrency) {
        validate(newCurrency.getCode(), newCurrency.getName(), newCurrency.getSign());
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

    public Currency getCurrencyByCode(String code) {
        return currencyDao.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("Currency with code=" + code + " not found"));
    }
}
