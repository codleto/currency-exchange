package servlet;

import dto.ExchangeRateDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;
import util.JsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter writer = resp.getWriter();

        String base = req.getParameter("base");
        String target = req.getParameter("target");
        String amountString = req.getParameter("amount");

        if (base == null || target == null || amountString == null ||
                base.isBlank() || target.isBlank() || amountString.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write("{\"message\": \"Нет данных\"}");
            return;
        }

        BigDecimal amount;

        try {
            amount = new BigDecimal(amountString);

            ExchangeRateDto exchange = exchangeRateService.exchange(base, target, amount);
            resp.setStatus(HttpServletResponse.SC_OK);

            JsonUtil.writeExchangeRateResponse(writer, exchange);

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write("{\"message\": \"" + e.getMessage() + "\"}");
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writer.write("{\"message\":" + e.getMessage() + "\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write("{\"message\":" + e.getMessage() + "\"}");
        }
    }
}
