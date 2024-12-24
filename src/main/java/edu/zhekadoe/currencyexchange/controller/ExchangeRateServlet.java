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

@WebServlet("/api/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
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
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) {

        try {
            String path = req.getPathInfo();
            String rate = getRate(req.getReader().readLine());
            ExchangeDto rateDto = ExchangeDto.of(path, rate);
            objectMapper.writeValue(resp.getWriter(), exchangeRateDao.update(rateDto));
        } catch (IllegalArgumentException e){
            throw new ApiBadRequestException(e.getMessage());
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
