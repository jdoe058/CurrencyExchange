package edu.zhekadoe.currencyexchange.services;

import edu.zhekadoe.currencyexchange.dto.CurrencyDto;
import edu.zhekadoe.currencyexchange.entity.Currency;
import edu.zhekadoe.currencyexchange.dao.CurrencyDao;
import edu.zhekadoe.currencyexchange.exception.ValidatorException;
import edu.zhekadoe.currencyexchange.validator.CurrencyValidator;
import edu.zhekadoe.currencyexchange.validator.ValidationResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyService {

    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyValidator currencyValidator = CurrencyValidator.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    public List<Currency> findAll() {
        return currencyDao.findAll();
    }

    public Currency findByCode(String currencyCode) {
        return currencyDao.findByCode(currencyCode);
    }

    public Currency create(CurrencyDto dto) {
        ValidationResult validationResult = currencyValidator.validate(dto);
        if (!validationResult.isValid()) {
            throw new ValidatorException(validationResult.getValidationErrors());
        }
        return currencyDao.create(dto);
    }
}
