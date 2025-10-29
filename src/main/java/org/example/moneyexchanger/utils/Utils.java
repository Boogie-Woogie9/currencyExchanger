package org.example.moneyexchanger.utils;

public class Utils {
    public static void validate(String code, String name, String sign) {
        if (code == null || code.isBlank()){
            throw new IllegalArgumentException("Currency code is invalid");
        }
        if (sign == null || sign.isBlank()){
            throw new IllegalArgumentException("Currency sign is invalid");
        }
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException("Currency name is invalid");
        }
        if (!code.matches("^[A-Z]{3}$")) {
            throw new IllegalArgumentException("Invalid updatedCurrency code format (must be 3 uppercase letters)");
        }
    }
}
