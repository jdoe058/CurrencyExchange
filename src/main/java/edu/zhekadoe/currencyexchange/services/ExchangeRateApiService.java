package edu.zhekadoe.currencyexchange.services;

import edu.zhekadoe.currencyexchange.ApiMapper;
import edu.zhekadoe.currencyexchange.dto.ExchangeDto;
import edu.zhekadoe.currencyexchange.dto.FindExchangeRateDto;
import edu.zhekadoe.currencyexchange.exception.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;



@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateApiService {
    public static final String FIELD_IS_MISSING_MESSAGE = "A required form field is missing";
    public static final String CURRENCY_PAIR_NOT_EXIST_MESSAGE = "One (or both) currencies from the currency pair do not exist in the database";
    public static final String EXCHANGE_RATE_NOT_FOUND_MESSAGE = "Exchange rate for the pair not found";
    public static final String CURRENCY_PAIR_ALREADY_EXISTS_MESSAGE = "A currency pair with this code already exists";


    private static final ExchangeRateApiService INSTANCE = new ExchangeRateApiService();
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ApiMapper mapper = ApiMapper.getInstance();

    public static ExchangeRateApiService getInstance() {
        return INSTANCE;
    }

    public String findAll() {
        return mapper.toJson(exchangeRateService.findAll());
    }

    public String findByCodes(FindExchangeRateDto dto) {
        return exchangeRateService.findByCodes(dto)
                .map(mapper::toJson)
                .orElseThrow(() -> new ApiNotFoundException(EXCHANGE_RATE_NOT_FOUND_MESSAGE));
    }

    public String create(ExchangeDto dto) {
        try {
            return mapper.toJson(exchangeRateService.create(dto));
        } catch (ValidatorException e) {
            throw new ApiBadRequestException(FIELD_IS_MISSING_MESSAGE);
        } catch (DaoNotFoundException e) {
            throw new ApiNotFoundException(CURRENCY_PAIR_NOT_EXIST_MESSAGE);
        } catch (DaoConflictException e) {
            throw new ApiConflictException(CURRENCY_PAIR_ALREADY_EXISTS_MESSAGE);
        }
    }

    public String update(ExchangeDto dto) {
        try {
            return mapper.toJson(exchangeRateService.update(dto));
        } catch (ValidatorException e) {
            throw new ApiBadRequestException(FIELD_IS_MISSING_MESSAGE);
        } catch (DaoNotFoundException e) {
            throw new ApiNotFoundException(CURRENCY_PAIR_NOT_EXIST_MESSAGE);
        } catch (DaoConflictException e) {
            throw new ApiConflictException(CURRENCY_PAIR_ALREADY_EXISTS_MESSAGE);
        }

    }
}
