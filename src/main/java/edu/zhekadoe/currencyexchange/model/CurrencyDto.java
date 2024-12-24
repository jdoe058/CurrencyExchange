package edu.zhekadoe.currencyexchange.model;

import java.util.Currency;

public record CurrencyDto(String name, String code, String sign) {

    private static final String NAME_REPLACE_REGEX = "(?i)(gay|sex|anal|oral|fuck|suck|beach)";
    private static final String NAME_REGEX = "[A-Za-z0-9 ]+";

    public static CurrencyDto of(String name, String code, String sign) {
        if (name == null || !name.matches(NAME_REGEX)) {
            throw new IllegalArgumentException("Currency name must contain latin letters, numbers and spaces");
        }

        if (code == null) {
            throw new IllegalArgumentException("Currency code must not be null");
        }

        code = code.toUpperCase();
        try {
            Currency.getInstance(code);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Currency code is invalid", e);
        }

        if (sign == null || sign.length() > 3) {
            throw new IllegalArgumentException("Currency sign must contain at most 3 characters");
        }

        if (sign.equalsIgnoreCase("sex") || sign.equalsIgnoreCase("gay")) {
            throw new IllegalArgumentException("Currency sign cannot be gay or sex");
        }

        return new CurrencyDto(
                name.replaceAll(NAME_REPLACE_REGEX, "***"),
                Currency.getInstance(code.toUpperCase()).getCurrencyCode(),
                sign);
    }
}
