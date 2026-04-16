package servlet;

import dto.CurrencyDto;
import entity.Currency;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static util.JsonUtil.extracted;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();

        List<CurrencyDto> currencyAll = currencyService.findAll();

        if (currencyAll.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write("{\"message\": \"База данных недоступна\"}");
            return;
        }

        writer.println("[");
        for (int i = 0; i < currencyAll.size(); i++) {
            CurrencyDto currencyDto = currencyAll.get(i);

            extracted(writer, currencyDto);

            if (i < currencyAll.size() - 1) {
                writer.println(",");
            }
        }
        writer.write("]");

        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter writer = resp.getWriter();

        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        if(code == null || code.isBlank()
                || name == null || name.isBlank()
                || sign == null || sign.isBlank()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write("{\"message\": \"Отсутствует нужное поле формы\"}");
            return;
        }

        Currency currency = new Currency(code, name, sign);

        try {
            CurrencyDto currencyDto = currencyService.save(currency);
            resp.setStatus(HttpServletResponse.SC_CREATED);

            extracted(writer, currencyDto);

        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            writer.write("{\"message\": \"Валюта с таким кодом уже существует\"}");
        }
    }
}



