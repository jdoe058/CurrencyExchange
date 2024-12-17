package edu.zhekadoe.currencyexchange.servlets;

import edu.zhekadoe.currencyexchange.services.CurrencyApiService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/currency/*")
public class CurrencyApiServlet extends HttpServlet {
    private final CurrencyApiService currencyApiService = CurrencyApiService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write(currencyApiService.findByPath(req.getPathInfo()));
    }
}
