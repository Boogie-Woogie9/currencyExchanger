package org.example.moneyexchanger.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {

    private Long id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;

    public ExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    @Override
    public String toString() {
        return String.format("{\n" +
                "\"id\": %d,\n" +
                "\"baseCurrency\": %s,\n" +
                "\"targetCurrency\": %s,\n" +
                "\"rate\": %f\n" +
                "}", id, baseCurrency, targetCurrency, rate);
    }
}
