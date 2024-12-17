package edu.zhekadoe.currencyexchange.entity;

import lombok.Data;
import lombok.NonNull;

public @Data(staticConstructor = "of") class Currency {
    private @NonNull Long id;
    private @NonNull String name;
    private @NonNull String code;
    private @NonNull String sign;
}
