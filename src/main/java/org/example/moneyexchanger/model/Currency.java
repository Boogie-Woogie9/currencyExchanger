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
    private String name;
    private String sign;
    public Currency(){}

    public Currency(String code, String name, String sign){
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("id =").append(id).
                append(", code = ").append(code)
                .append(", fullName = ").append(name)
                .append(", sign = ").append(name).toString();
    }
}
