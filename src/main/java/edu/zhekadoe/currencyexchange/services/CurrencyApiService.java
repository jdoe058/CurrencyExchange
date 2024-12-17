package edu.zhekadoe.currencyexchange.services;

import edu.zhekadoe.currencyexchange.ApiMapper;
import edu.zhekadoe.currencyexchange.exception.*;
import edu.zhekadoe.currencyexchange.dto.CurrencyDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static edu.zhekadoe.currencyexchange.utils.ApiServletPath.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyApiService {
    public static final String CURRENCY_CODE_ALREADY_EXISTS_MESSAGE = "A currency with this code already exists.";
    public static final String FIELD_IS_MISSING_MESSAGE = "A required form field is missing";

    private static final CurrencyApiService INSTANCE = new CurrencyApiService();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ApiMapper mapper = ApiMapper.getInstance();

    public static CurrencyApiService getInstance() {
        return INSTANCE;
    }

    public String findAll() {
        return mapper.toJson(currencyService.findAll());
    }

    public String findByPath(String path) {
        validatePath(path, 3);
        String currencyCode = path.substring(BEGIN_BASE_INDEX);

        return mapper.toJson(currencyService.findByCode(currencyCode));
    }

    public String create(CurrencyDto dto) {
        try {
            return mapper.toJson(currencyService.create(dto));
        } catch (ValidatorException e) {
            throw new ApiBadRequestException(FIELD_IS_MISSING_MESSAGE);
        } catch (DaoConflictException e) {
            throw new ApiConflictException(CURRENCY_CODE_ALREADY_EXISTS_MESSAGE);
        }
    }
}
