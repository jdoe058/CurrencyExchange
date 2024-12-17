package edu.zhekadoe.currencyexchange.services;

import edu.zhekadoe.currencyexchange.dao.ExchangeRateDao;
import edu.zhekadoe.currencyexchange.dto.ExchangeDto;
import edu.zhekadoe.currencyexchange.dto.FindExchangeRateDto;
import edu.zhekadoe.currencyexchange.entity.ExchangeRate;
import edu.zhekadoe.currencyexchange.exception.*;
import edu.zhekadoe.currencyexchange.validator.ExchangeRateValidator;
import edu.zhekadoe.currencyexchange.validator.ValidationResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;



@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateService {

    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateValidator exchangeRateValidator = ExchangeRateValidator.getInstance();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();


    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    public List<ExchangeRate> findAll() {
        return exchangeRateDao.findAll();
    }

    public Optional<ExchangeRate> findByCodes(FindExchangeRateDto findExchangeRateDto) {
        return exchangeRateDao.findByCodes(findExchangeRateDto);
    }

    public ExchangeRate create(ExchangeDto dto) {
        ValidationResult validate = exchangeRateValidator.validate(dto);
        if (!validate.isValid()) {
            throw new ValidatorException(validate.getValidationErrors());
        }
        return exchangeRateDao.create(dto);
    }

    public ExchangeRate update(ExchangeDto dto) {
        ValidationResult validate = exchangeRateValidator.validate(dto);
        if (!validate.isValid()) {
            throw new ValidatorException(validate.getValidationErrors());
        }
        return exchangeRateDao.update(dto);
    }
}
