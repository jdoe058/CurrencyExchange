package edu.zhekadoe.currencyexchange.servlets;

import edu.zhekadoe.currencyexchange.exception.DaoConflictException;
import edu.zhekadoe.currencyexchange.services.CurrencyService;
import edu.zhekadoe.currencyexchange.dto.CurrencyDto;
import edu.zhekadoe.currencyexchange.exception.ValidatorException;
import edu.zhekadoe.currencyexchange.validator.ValidationError;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("currencies", currencyService.findAll());
        req.getRequestDispatcher("/WEB-INF/jsp/currencies.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CurrencyDto currencyDto = CurrencyDto.of(
                req.getParameter("name"),
                req.getParameter("code"),
                req.getParameter("sign"));
        try {
            currencyService.create(currencyDto);
            doGet(req, resp);
        } catch (ValidatorException e) {
            req.setAttribute("errors", e.getValidationErrors());
            doGet(req, resp);
        } catch (DaoConflictException e) {
            req.setAttribute("errors", List.of(
                    ValidationError.of("db.conflict", "Currencies already exists")));
            doGet(req, resp);
        }
    }
}
