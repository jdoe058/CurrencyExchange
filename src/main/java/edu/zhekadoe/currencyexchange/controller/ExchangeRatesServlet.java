package edu.zhekadoe.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.zhekadoe.currencyexchange.model.ExchangeRateDao;
import edu.zhekadoe.currencyexchange.model.ExchangeDto;
import edu.zhekadoe.currencyexchange.exception.*;
import edu.zhekadoe.currencyexchange.validator.ExchangeRateValidator;
import edu.zhekadoe.currencyexchange.validator.ValidationResult;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    public static final String FIELD_IS_MISSING_MESSAGE = "A required form field is missing";

    private final ExchangeRateValidator exchangeRateValidator = ExchangeRateValidator.getInstance();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            objectMapper.writeValue(resp.getWriter(), exchangeRateDao.findAll());
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        ExchangeDto rateDto = ExchangeDto.of(
                req.getParameter("baseCurrencyCode"),
                req.getParameter("targetCurrencyCode"),
                req.getParameter("rate"));

        ValidationResult validate = exchangeRateValidator.validate(rateDto);
        if (!validate.isValid()) {
            throw new ApiBadRequestException(FIELD_IS_MISSING_MESSAGE);
        }

        try {
            objectMapper.writeValue(resp.getWriter(), exchangeRateDao.create(rateDto));
        } catch (DaoNotFoundException e) {
            throw new ApiNotFoundException(e.getMessage());
        } catch (DaoConflictException e) {
            throw new ApiConflictException(e.getMessage());
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }
}
