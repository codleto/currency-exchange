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
import java.util.Optional;

@WebServlet("/exchange-rate/*")
public class ExchangeRateServlet extends HttpServlet {

    ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter writer = resp.getWriter();

        String pathInfo = req.getPathInfo();
        String code = pathInfo.substring(1);

        String baseCode = code.substring(0,3);
        String targetCode = code.substring(3);

        Optional<ExchangeRateDto> byCode = exchangeRateService.findByCode(baseCode, targetCode);
        if(byCode.isEmpty()){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write("{\"message\": \"Ошибка! База данных недоступна\"}");

            return;
        }

        ExchangeRateDto exchangeRateDto = byCode.get();

        JsonUtil.writeExchangeRateResponse(writer, exchangeRateDto);
        resp.setStatus(HttpServletResponse.SC_OK);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter writer = resp.getWriter();

        String baseCode = req.getParameter("base");
        String targetCode = req.getParameter("target");
        String rateString = req.getParameter("rate");

        if(baseCode == null || baseCode.isBlank() || targetCode == null || targetCode.isBlank()
                || rateString == null || rateString.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write("{\"message\": \"Данные заполнены не полностью\"}");
            return;
        }

        BigDecimal rate;

        try {
            rate = new BigDecimal(rateString);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write("{\"message\": \"Неправильный формат rate\"}");
            return;
        }

        ExchangeRateDto save = exchangeRateService.save(baseCode, targetCode, rate);

        JsonUtil.writeExchangeRateResponse(writer,save);

        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter writer = resp.getWriter();

        String pathInfo = req.getPathInfo();
        String code = pathInfo.substring(1);

        String base = code.substring(0,3);
        String target = code.substring(3);
        String rateString = req.getParameter("rate");

        if(base.isBlank() || target.isBlank() || rateString == null || rateString.isBlank()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write("{\"message\": \"Данные заполнены не полностью\"}");
            return;
        }

        BigDecimal rate;

        try {
            rate = new BigDecimal(rateString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        exchangeRateService.update(base, target, rate);
        resp.setStatus(HttpServletResponse.SC_OK);

    }

}
