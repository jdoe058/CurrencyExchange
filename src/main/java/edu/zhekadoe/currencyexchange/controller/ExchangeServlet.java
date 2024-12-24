package edu.zhekadoe.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.zhekadoe.currencyexchange.model.ExchangeDao;
import edu.zhekadoe.currencyexchange.model.ExchangeDto;
import edu.zhekadoe.currencyexchange.exception.ApiBadRequestException;
import edu.zhekadoe.currencyexchange.exception.ApiException;
import edu.zhekadoe.currencyexchange.validator.ExchangeRateValidator;
import edu.zhekadoe.currencyexchange.validator.ValidationResult;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/exchange")
public class ExchangeServlet extends HttpServlet {

    public static final String FIELD_IS_MISSING_MESSAGE = "A required form field is missing";

    private final ExchangeRateValidator exchangeRateValidator = ExchangeRateValidator.getInstance();
    private final ExchangeDao exchangeDao = ExchangeDao.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        ExchangeDto dto = ExchangeDto.of(
                req.getParameter("from"),
                req.getParameter("to"),
                req.getParameter("amount")
        );

        ValidationResult validate = exchangeRateValidator.validate(dto);
        if (!validate.isValid()) {
            throw new ApiBadRequestException(FIELD_IS_MISSING_MESSAGE);
        }

        try {
            objectMapper.writeValue(resp.getWriter(), exchangeDao.exchange(dto));
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }
}
