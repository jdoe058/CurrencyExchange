package edu.zhekadoe.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.zhekadoe.currencyexchange.model.ExchangeDao;
import edu.zhekadoe.currencyexchange.model.ExchangeDto;
import edu.zhekadoe.currencyexchange.exception.ApiBadRequestException;
import edu.zhekadoe.currencyexchange.exception.ApiException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/exchange")
public class ExchangeServlet extends HttpServlet {

    private final ExchangeDao exchangeDao = ExchangeDao.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        try {
            ExchangeDto dto = ExchangeDto.of(
                    req.getParameter("from"),
                    req.getParameter("to"),
                    req.getParameter("amount")
            );
            objectMapper.writeValue(resp.getWriter(), exchangeDao.exchange(dto));
        } catch (IllegalArgumentException e){
            throw new ApiBadRequestException(e.getMessage());
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }
}
