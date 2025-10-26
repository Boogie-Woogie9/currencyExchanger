package org.example.moneyexchanger;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.moneyexchanger.model.Currency;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDto {
    private Currency base;
    private Currency target;
    private BigDecimal rate;
//    private BigDecimal amount;
//    private BigDecimal convertedAmount;
}
