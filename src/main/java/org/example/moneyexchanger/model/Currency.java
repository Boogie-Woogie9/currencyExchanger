package org.example.moneyexchanger.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Currency {

    private Long id;
    private String code;
    private String fullName;
    private String sign;
    public Currency(){}

    public Currency(String code, String fullName, String sign){
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("id =").append(id).
                append(", code = ").append(code)
                .append(", fullName = ").append(fullName)
                .append(", sign = ").append(fullName).toString();
    }
}
