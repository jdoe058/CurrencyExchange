package edu.zhekadoe.currencyexchange.model;

public record CurrencyPairCodesDto(String base, String target) {
    private static final int BEGIN_BASE_INDEX = 1;
    private static final int BEGIN_TARGET_INDEX = 4;
    private static final String CURRENCY_REGEX = "[A-Za-z]{3}";
    private static final String PATH_REGEX = "/" + CURRENCY_REGEX.repeat(2);


    public static CurrencyPairCodesDto of(String base, String target) {

        if (base == null || !base.matches(CURRENCY_REGEX)) {
            throw new IllegalArgumentException("Base currency is not valid");
        }

        if (target == null || !target.matches(CURRENCY_REGEX)) {
            throw new IllegalArgumentException("Target currency is not valid");
        }

        return new CurrencyPairCodesDto(base.toUpperCase(), target.toUpperCase());
    }

    public static CurrencyPairCodesDto of(String path) {

        if (path == null || !path.matches(PATH_REGEX)) {
            throw new IllegalArgumentException("Path currency is not valid");
        }

        return CurrencyPairCodesDto.of(
                path.substring(BEGIN_BASE_INDEX, BEGIN_TARGET_INDEX),
                path.substring(BEGIN_TARGET_INDEX));
    }
}
