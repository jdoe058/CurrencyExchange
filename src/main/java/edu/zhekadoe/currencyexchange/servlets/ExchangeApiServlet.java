package edu.zhekadoe.currencyexchange.servlets;

import edu.zhekadoe.currencyexchange.ApiMapper;
import edu.zhekadoe.currencyexchange.dao.ExchangeDao;
import edu.zhekadoe.currencyexchange.dto.ExchangeDto;
import edu.zhekadoe.currencyexchange.exception.ApiBadRequestException;
import edu.zhekadoe.currencyexchange.validator.ExchangeRateValidator;
import edu.zhekadoe.currencyexchange.validator.ValidationResult;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/exchange")
public class ExchangeApiServlet extends HttpServlet {
    public static final String FIELD_IS_MISSING_MESSAGE = "A required form field is missing";

    private final ExchangeRateValidator exchangeRateValidator = ExchangeRateValidator.getInstance();
    private final ExchangeDao exchangeDao = ExchangeDao.getInstance();
    private final ApiMapper mapper = ApiMapper.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeDto dto = ExchangeDto.of(
                req.getParameter("from"),
                req.getParameter("to"),
                req.getParameter("amount")
        );

        ValidationResult validate = exchangeRateValidator.validate(dto);
        if (!validate.isValid()) {
            throw new ApiBadRequestException(FIELD_IS_MISSING_MESSAGE);
        }

        resp.getWriter().write(mapper.toJson(exchangeDao.exchange(dto)));
    }
}
