package edu.zhekadoe.currencyexchange.servlets;

import edu.zhekadoe.currencyexchange.dto.ExchangeDto;
import edu.zhekadoe.currencyexchange.exception.ApiBadRequestException;
import edu.zhekadoe.currencyexchange.exception.ValidatorException;
import edu.zhekadoe.currencyexchange.services.ExchangeApiService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/exchange")
public class ExchangeApiServlet extends HttpServlet {
    public static final String FIELD_IS_MISSING_MESSAGE = "A required form field is missing";
    private final ExchangeApiService exchangeApiService = ExchangeApiService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeDto dto = ExchangeDto.of(
                req.getParameter("from"),
                req.getParameter("to"),
                req.getParameter("amount")
        );
        try {
            resp.getWriter().write(exchangeApiService.get(dto));
        } catch (ValidatorException e) {
            throw new ApiBadRequestException(FIELD_IS_MISSING_MESSAGE);
        }
    }
}
