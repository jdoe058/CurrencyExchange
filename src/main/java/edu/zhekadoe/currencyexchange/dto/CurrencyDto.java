package edu.zhekadoe.currencyexchange.dto;

import lombok.Value;

@Value(staticConstructor = "of")
public class CurrencyDto {
    String name;
    String code;
    String sign;
}
