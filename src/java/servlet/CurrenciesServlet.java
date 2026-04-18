package servlet;

import dto.CurrencyDto;
import entity.Currency;
import exception.BadRequestException;
import exception.ConflictException;
import exception.DatabaseException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;
import util.ResponseUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static util.JsonUtil.writeCurrencyJson;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();

        try {
            List<CurrencyDto> currencyAll = currencyService.findAll();
            resp.setStatus(HttpServletResponse.SC_OK);

            writer.println("[");
            for (int i = 0; i < currencyAll.size(); i++) {
                CurrencyDto currencyDto = currencyAll.get(i);

                writeCurrencyJson(writer, currencyDto);

                if (i < currencyAll.size() - 1) {
                    writer.println(",");
                }
            }
            writer.write("]");

        } catch (DatabaseException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ResponseUtil.writeError(resp, "Ошибка базы данных");
        }
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
            ResponseUtil.writeError(resp, "Отсутствует нужное поле формы");
            return;
        }

        Currency currency = new Currency(code, name, sign);

        try {
            CurrencyDto currencyDto = currencyService.save(currency);
            resp.setStatus(HttpServletResponse.SC_CREATED);

            writeCurrencyJson(writer, currencyDto);

        } catch (BadRequestException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ResponseUtil.writeError(resp, e.getMessage());

        } catch (ConflictException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            ResponseUtil.writeError(resp, e.getMessage());

        } catch (DatabaseException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ResponseUtil.writeError(resp, "Ошибка базы данных");
        }
    }
}



