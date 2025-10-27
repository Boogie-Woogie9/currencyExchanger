package org.example.moneyexchanger.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.moneyexchanger.model.Currency;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExchangeRateDto {
    private Currency base;
    private Currency target;
    private BigDecimal rate;
//    private BigDecimal amount;
//    private BigDecimal convertedAmount;
}
