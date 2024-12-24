package edu.zhekadoe.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.zhekadoe.currencyexchange.model.CurrencyDao;
import edu.zhekadoe.currencyexchange.exception.*;
import edu.zhekadoe.currencyexchange.model.CurrencyDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            objectMapper.writeValue(resp.getWriter(), currencyDao.findAll());
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            CurrencyDto currencyDto = CurrencyDto.of(
                    req.getParameter("name"),
                    req.getParameter("code"),
                    req.getParameter("sign"));
            objectMapper.writeValue(resp.getWriter(), currencyDao.create(currencyDto));
        } catch (IllegalArgumentException e) {
            throw new ApiBadRequestException(e.getMessage());
        } catch (DaoConflictException e) {
            throw new ApiConflictException(e.getMessage());
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }
}
