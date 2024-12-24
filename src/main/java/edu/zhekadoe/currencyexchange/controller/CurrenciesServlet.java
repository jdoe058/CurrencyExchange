package edu.zhekadoe.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.zhekadoe.currencyexchange.model.CurrencyDao;
import edu.zhekadoe.currencyexchange.exception.*;
import edu.zhekadoe.currencyexchange.model.CurrencyDto;
import edu.zhekadoe.currencyexchange.validator.CurrencyValidator;
import edu.zhekadoe.currencyexchange.validator.ValidationResult;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/currencies")
public class CurrenciesServlet extends HttpServlet {

    public static final String FIELD_IS_MISSING_MESSAGE = "A required form field is missing";

    private final CurrencyValidator currencyValidator = CurrencyValidator.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            objectMapper.writeValue(resp.getWriter(), currencyDao.findAll());
        } catch (Exception e) {
            throw new ApiException("");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        CurrencyDto currencyDto = CurrencyDto.of(
                req.getParameter("name"),
                req.getParameter("code"),
                req.getParameter("sign"));

        ValidationResult validationResult = currencyValidator.validate(currencyDto);
        if (!validationResult.isValid()) {
            throw new ApiBadRequestException(FIELD_IS_MISSING_MESSAGE);
        }

        try {
            objectMapper.writeValue(resp.getWriter(), currencyDao.create(currencyDto));
        } catch (DaoConflictException e) {
            throw new ApiConflictException(e.getMessage());
        } catch (Exception e) {
            throw new ApiException("");
        }
    }
}
