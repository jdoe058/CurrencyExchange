package edu.zhekadoe.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.zhekadoe.currencyexchange.model.ExchangeRateDao;
import edu.zhekadoe.currencyexchange.model.ExchangeDto;
import edu.zhekadoe.currencyexchange.exception.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

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

        try {
            ExchangeDto rateDto = ExchangeDto.of(
                    req.getParameter("baseCurrencyCode"),
                    req.getParameter("targetCurrencyCode"),
                    req.getParameter("rate"));
            objectMapper.writeValue(resp.getWriter(), exchangeRateDao.create(rateDto));
        } catch (IllegalArgumentException e) {
            throw new ApiBadRequestException(e.getMessage());
        } catch (DaoNotFoundException e) {
            throw new ApiNotFoundException(e.getMessage());
        } catch (DaoConflictException e) {
            throw new ApiConflictException(e.getMessage());
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }
}
