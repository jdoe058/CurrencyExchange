package edu.zhekadoe.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.zhekadoe.currencyexchange.model.ExchangeRateDao;
import edu.zhekadoe.currencyexchange.model.CurrencyPairCodesDto;
import edu.zhekadoe.currencyexchange.model.ExchangeDto;
import edu.zhekadoe.currencyexchange.exception.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static edu.zhekadoe.currencyexchange.utils.ApiServletPath.*;

@WebServlet("/api/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final int EXCHANGE_RATE_PATH_SIZE = 6;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        try {
            String path = req.getPathInfo();
            CurrencyPairCodesDto findDto = CurrencyPairCodesDto.of(path);
            objectMapper.writeValue(resp.getWriter(), exchangeRateDao.findByCodes(findDto));
        } catch (DaoNotFoundException e) {
            throw new ApiNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
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

        try {
            objectMapper.writeValue(resp.getWriter(), exchangeRateDao.update(rateDto));
        } catch (DaoNotFoundException e) {
            throw new ApiNotFoundException(e.getMessage());
        } catch (DaoConflictException e) {
            throw new ApiConflictException(e.getMessage());
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }


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
