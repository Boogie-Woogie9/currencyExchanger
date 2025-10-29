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
    private static final String BASE_CURRENCY = "USD";


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

    public BigDecimal getExchangeRate(String fromCode, String toCode){
        if (fromCode.equals(toCode)){
            return BigDecimal.ONE;
        }

        // Прямой курс
        Optional<ExchangeRate> direct = exchangeRateDao.findByCodes(fromCode, toCode);
        if (direct.isPresent()){
            return direct.get().getRate();
        }

        // Обратный курс
        Optional<ExchangeRate> reverse = exchangeRateDao.findByCodes(toCode, fromCode);
        if (reverse.isPresent()){
            BigDecimal rate = reverse.get().getRate();
            return BigDecimal.ONE.divide(rate, 6);
        }

        // Кросс-курс
        Optional<ExchangeRate> usdFrom = exchangeRateDao.findByCodes(BASE_CURRENCY, fromCode);
        Optional<ExchangeRate> usdTo = exchangeRateDao.findByCodes(BASE_CURRENCY, toCode);

        if (usdFrom.isPresent() && usdTo.isPresent()) {
            BigDecimal rateA = usdFrom.get().getRate(); // USD->A
            BigDecimal rateB = usdTo.get().getRate();   // USD->B

            // Курс A->B = (USD->A) / (USD->B)
            return rateB.divide(rateA, 6);
        }

        throw new RuntimeException("Exchange rate not found for pair " + fromCode + " → " + toCode);
    }

    // Коневертация суммы по курсу обмена
    public BigDecimal convertAmount(String fromCode, String toCode, BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Amount must be positive.");
        }
        BigDecimal rate = getExchangeRate(fromCode, toCode);
        BigDecimal result = amount.multiply(rate);

        return result.setScale(6);
    }
}
