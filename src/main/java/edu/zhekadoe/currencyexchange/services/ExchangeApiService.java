package edu.zhekadoe.currencyexchange.services;

import edu.zhekadoe.currencyexchange.ApiMapper;
import edu.zhekadoe.currencyexchange.dto.ConvertedDto;
import edu.zhekadoe.currencyexchange.dto.FindExchangeRateDto;
import edu.zhekadoe.currencyexchange.dto.ExchangeDto;
import edu.zhekadoe.currencyexchange.entity.ExchangeRate;
import edu.zhekadoe.currencyexchange.exception.ApiBadRequestException;
import edu.zhekadoe.currencyexchange.exception.ApiNotFoundException;
import edu.zhekadoe.currencyexchange.exception.DaoNotFoundException;
import edu.zhekadoe.currencyexchange.exception.ValidatorException;
import edu.zhekadoe.currencyexchange.validator.ExchangeRateValidator;
import edu.zhekadoe.currencyexchange.validator.ValidationResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeApiService {

    private static final String EXCHANGE_RATE_NOT_FOUND_MESSAGE = "Exchange rate not found";
    private static final String CROSS_CURRENCY_CODE = "RUB";
    private static final int AMOUNT_OPERATION_SCALE = 6;
    private static final ExchangeApiService INSTANCE = new ExchangeApiService();

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ExchangeRateValidator exchangeRateValidator = ExchangeRateValidator.getInstance();
    private final ApiMapper apiMapper = ApiMapper.getInstance();

    public String get(ExchangeDto dto) {
        ValidationResult validate = exchangeRateValidator.validate(dto);
        if (!validate.isValid()) {
            throw new ValidatorException(validate.getValidationErrors());
        }
        BigDecimal amount = new BigDecimal(dto.getValue());

        String baseCode = dto.getBaseCurrencyCode();
        String targetCode = dto.getTargetCurrencyCode();

        ConvertedDto convertedDto = findExchangeRate(baseCode, targetCode)
                .map(exchangeRate -> createDirectConvertedDto(exchangeRate, amount))
                .or(() -> findExchangeRate(targetCode, baseCode).map(
                        exchangeRate -> createReverseConvertedDto(exchangeRate, amount)))
                .orElseGet(() -> createCrossConvertedDto(baseCode, targetCode, amount));

        return apiMapper.toJson(convertedDto);
    }

    public static ExchangeApiService getInstance() {
        return INSTANCE;
    }

    private ConvertedDto createDirectConvertedDto(@NonNull ExchangeRate exchangeRate, BigDecimal amount) {
        return ConvertedDto.of(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate(),
                amount);
    }

    private ConvertedDto createReverseConvertedDto(@NonNull ExchangeRate exchangeRate, BigDecimal amount) {

        BigDecimal reverseRate = BigDecimal.ONE.divide(exchangeRate.getRate(),
                AMOUNT_OPERATION_SCALE, RoundingMode.HALF_UP);

        return ConvertedDto.of(
                exchangeRate.getTargetCurrency(),
                exchangeRate.getBaseCurrency(),
                reverseRate,
                amount);
    }

    private ConvertedDto createCrossConvertedDto(String baseCode, String targetCode, BigDecimal amount) {
        ExchangeRate baseRate = findCrossExchangeRate(baseCode);
        ExchangeRate targetRate = findCrossExchangeRate(targetCode);

        BigDecimal crossRate = baseRate.getRate().divide(targetRate.getRate(),
                AMOUNT_OPERATION_SCALE, RoundingMode.HALF_UP);

        return ConvertedDto.of(
                baseRate.getTargetCurrency(),
                targetRate.getTargetCurrency(),
                crossRate,
                amount);
    }

    private Optional<ExchangeRate> findExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        return exchangeRateService.findByCodes(FindExchangeRateDto.of(
                baseCurrencyCode.toUpperCase(),
                targetCurrencyCode.toUpperCase()));
    }

    private ExchangeRate findCrossExchangeRate(String rate) {
        return exchangeRateService.findByCodes(FindExchangeRateDto.of(CROSS_CURRENCY_CODE, rate.toUpperCase()))
                .orElseThrow(() -> new ApiNotFoundException(EXCHANGE_RATE_NOT_FOUND_MESSAGE));
    }
}
