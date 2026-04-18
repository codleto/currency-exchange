package servlet;

import dto.ExchangeRateDto;
import exception.BadRequestException;
import exception.DatabaseException;
import exception.NotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;
import util.JsonUtil;
import util.ResponseUtil;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter writer = resp.getWriter();

        String base = req.getParameter("from");
        String target = req.getParameter("to");
        String amountString = req.getParameter("amount");

        if (base == null || target == null || amountString == null ||
                base.isBlank() || target.isBlank() || amountString.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ResponseUtil.writeError(resp, "Данные заполнены не полностью");
            return;
        }

        try {
            ExchangeRateDto exchange = exchangeRateService.exchange(base, target, amountString);

            resp.setStatus(HttpServletResponse.SC_OK);
            JsonUtil.writeExchangeRateJson(writer, exchange);

        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ResponseUtil.writeError(resp, e.getMessage());

        } catch (BadRequestException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ResponseUtil.writeError(resp, e.getMessage());

        } catch (DatabaseException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ResponseUtil.writeError(resp, "Ошибка базы данных");
        }
    }
}
