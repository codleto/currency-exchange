package servlet;

import dto.ExchangeRateDto;
import exception.DatabaseException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;
import util.JsonUtil;
import util.ResponseUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/exchange-rates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();

        try {
            List<ExchangeRateDto> all = exchangeRateService.findAll();

            resp.setStatus(HttpServletResponse.SC_OK);

            writer.println("[");
            for (int i = 0; i < all.size(); i++) {
                ExchangeRateDto exchangeRateDto = all.get(i);

                JsonUtil.writeExchangeRateJson(writer, exchangeRateDto);

                if (i < all.size() - 1) {
                    writer.println(",");
                }
                writer.println();
            }
            writer.println("]");

        } catch (DatabaseException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ResponseUtil.writeError(resp, "Ошибка базы данных");
        }
    }
}

