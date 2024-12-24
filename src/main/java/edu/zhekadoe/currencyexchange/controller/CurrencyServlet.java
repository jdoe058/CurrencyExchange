package edu.zhekadoe.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.zhekadoe.currencyexchange.exception.ApiBadRequestException;
import edu.zhekadoe.currencyexchange.model.CurrencyDao;
import edu.zhekadoe.currencyexchange.exception.ApiException;
import edu.zhekadoe.currencyexchange.exception.ApiNotFoundException;
import edu.zhekadoe.currencyexchange.exception.DaoNotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getPathInfo();

        if (path == null || !path.matches("/[A-Za-z]{3}")) {
           throw new ApiBadRequestException("Invalid path");
        }

        String currencyCode = path.substring(1).toUpperCase();

        try {
            objectMapper.writeValue(resp.getWriter(), currencyDao.findByCode(currencyCode));
        } catch (DaoNotFoundException e) {
            throw new ApiNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }
}
