package org.example.moneyexchanger.service;

import org.example.moneyexchanger.dto.ExchangeRateDto;
import org.example.moneyexchanger.dao.CurrencyDao;
import org.example.moneyexchanger.dao.ExchangeRateDao;
import org.example.moneyexchanger.model.Currency;
import org.example.moneyexchanger.model.ExchangeRate;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ExchangeRateService {

    public ExchangeRateDao exchangeRateDao;
    public CurrencyDao currencyDao;

    public ExchangeRateService(ExchangeRateDao exchangeRateDao, CurrencyDao currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyDao = currencyDao;
    }

    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRateDao.findAll() ;
    }

    public Optional<ExchangeRate> getExchangeRateByCodes(String base, String target){
        return exchangeRateDao.findByCodes(base, target);
    }

    public ExchangeRate getExchangeRateById(Long id) {
        return exchangeRateDao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ExchangeRate with id =" + id + " not found"));
    }

    public void createExchangeRate(ExchangeRateDto dto) {

        Currency base = currencyDao.findById(Long.valueOf(dto.getBaseId()))
                .orElseThrow(() -> new IllegalArgumentException("Base currency not found."));
        Currency target = currencyDao.findById(Long.valueOf(dto.getTargetId()))
                .orElseThrow(() -> new IllegalArgumentException("Target currency not found."));

        if (dto.getRate() == null || dto.getRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive.");
        }

        ExchangeRate newExchangeRate = new ExchangeRate(base, target, dto.getRate());
        exchangeRateDao.save(newExchangeRate);
    }

    public void updateExchangeRate(ExchangeRate entity){
        if (entity.getRate() == null || entity.getRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Rate must be positive.");
        }
        if (exchangeRateDao.findById(entity.getId()).isEmpty()) {
            throw new NoSuchElementException("Exchange rate not found for id=" + entity.getId());
        }
        exchangeRateDao.update(entity);
    }

    public void deleteExchangeRateById(Long id){
        if (exchangeRateDao.findById(id).isEmpty()) {
            throw new NoSuchElementException("Exchange rate not found");
        }
        exchangeRateDao.delete(id);
    }
}
