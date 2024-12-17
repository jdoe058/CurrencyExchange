package edu.zhekadoe.currencyexchange.servlets;

import edu.zhekadoe.currencyexchange.dto.ExchangeDto;
import edu.zhekadoe.currencyexchange.services.ExchangeRateApiService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/exchangeRates")
public class ExchangeRatesApiServlet extends HttpServlet {
    private final ExchangeRateApiService exchangeRateApiService = ExchangeRateApiService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write(exchangeRateApiService.findAll());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ExchangeDto rateDto = ExchangeDto.of(
                req.getParameter("baseCurrencyCode"),
                req.getParameter("targetCurrencyCode"),
                req.getParameter("rate"));

        resp.getWriter().write(exchangeRateApiService.create(rateDto));
    }
}
