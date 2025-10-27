package org.example.moneyexchanger.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExchangeRateDto {
    private String baseId;
    private String targetId;
    private BigDecimal rate;
//    private BigDecimal amount;
//    private BigDecimal convertedAmount;
}
