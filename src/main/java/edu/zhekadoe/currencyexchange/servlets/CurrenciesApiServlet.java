package edu.zhekadoe.currencyexchange.servlets;

import edu.zhekadoe.currencyexchange.services.CurrencyApiService;
import edu.zhekadoe.currencyexchange.dto.CurrencyDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/currencies")
public class CurrenciesApiServlet extends HttpServlet {
    private final CurrencyApiService currencyApiService = CurrencyApiService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write(currencyApiService.findAll());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CurrencyDto currencyDto = CurrencyDto.of(
                req.getParameter("name"),
                req.getParameter("code"),
                req.getParameter("sign"));

        resp.getWriter().write(currencyApiService.create(currencyDto));
    }
}
