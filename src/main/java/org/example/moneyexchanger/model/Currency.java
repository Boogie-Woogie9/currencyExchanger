package org.example.moneyexchanger.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Currency {

    private int id;
    private String code;
    private String fullName;
    private String sign;
    public Currency(){}

    public Currency(int id, String code, String fullName, String sign){
        this.id = id;
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
