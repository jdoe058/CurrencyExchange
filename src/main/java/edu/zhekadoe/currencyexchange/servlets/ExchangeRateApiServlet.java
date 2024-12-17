package edu.zhekadoe.currencyexchange.servlets;

import edu.zhekadoe.currencyexchange.dto.FindExchangeRateDto;
import edu.zhekadoe.currencyexchange.dto.ExchangeDto;
import edu.zhekadoe.currencyexchange.services.ExchangeRateApiService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static edu.zhekadoe.currencyexchange.utils.ApiServletPath.*;

@WebServlet("/api/exchangeRate/*")
public class ExchangeRateApiServlet extends HttpServlet {
    private static final int EXCHANGE_RATE_PATH_SIZE = 6;
    private final ExchangeRateApiService exchangeRateApiService = ExchangeRateApiService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        validatePath(path, EXCHANGE_RATE_PATH_SIZE);

        FindExchangeRateDto findDto = FindExchangeRateDto.of(
                path.substring(BEGIN_BASE_INDEX, BEGIN_TARGET_INDEX).toUpperCase(),
                path.substring(BEGIN_TARGET_INDEX).toUpperCase());

        resp.getWriter().write(exchangeRateApiService.findByCodes(findDto));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        validatePath(path, EXCHANGE_RATE_PATH_SIZE);

        String rate =  getRate(req.getReader().readLine());

        ExchangeDto rateDto = ExchangeDto.of(
                path.substring(BEGIN_BASE_INDEX, BEGIN_TARGET_INDEX).toUpperCase(),
                path.substring(BEGIN_TARGET_INDEX).toUpperCase(),
                rate);
        resp.getWriter().write(exchangeRateApiService.update(rateDto));
    }

    private static String getRate(String params) {
        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("Request body is empty or null.");
        }

        for (String param : params.split("&")) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && "rate".equals(keyValue[0])) {
                return keyValue[1];
            }
        }
        throw new IllegalArgumentException("Rate parameter not found.");
    }
}
