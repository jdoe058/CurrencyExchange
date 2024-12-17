package edu.zhekadoe.currencyexchange.servlets;


import edu.zhekadoe.currencyexchange.dto.ExchangeDto;
import edu.zhekadoe.currencyexchange.exception.DaoConflictException;
import edu.zhekadoe.currencyexchange.exception.DaoNotFoundException;
import edu.zhekadoe.currencyexchange.exception.ValidatorException;
import edu.zhekadoe.currencyexchange.services.ExchangeRateService;
import edu.zhekadoe.currencyexchange.validator.ValidationError;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("exchangeRates", exchangeRateService.findAll());
        req.getRequestDispatcher("/WEB-INF/jsp/exchangeRates.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ExchangeDto rateDto = ExchangeDto.of(
                req.getParameter("baseCurrencyCode"),
                req.getParameter("targetCurrencyCode"),
                req.getParameter("rate"));
        try {
            exchangeRateService.create(rateDto);
            doGet(req, resp);
        } catch (ValidatorException e) {
            req.setAttribute("errors", e.getValidationErrors());
            doGet(req, resp);
        }catch (DaoNotFoundException e) {
            req.setAttribute("errors", List.of(
                    ValidationError.of("db.notFound", "Currencies not found")));
            doGet(req, resp);
        } catch (DaoConflictException e) {
            req.setAttribute("errors", List.of(
                    ValidationError.of("db.conflict", "Currencies already exists")));
            doGet(req, resp);
        }
    }
}
