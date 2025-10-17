package org.example.moneyExchanger.model;

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
}
