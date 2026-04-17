package servlet;

import dto.CurrencyDto;
import exception.BadRequestException;
import exception.DatabaseException;
import exception.NotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;
import util.ResponseUtil;

import java.io.IOException;
import java.io.PrintWriter;

import static util.JsonUtil.writeCurrencyJson;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String pathInfo = req.getPathInfo();
        String code = pathInfo.substring(1);

        PrintWriter writer = resp.getWriter();

        try {
            CurrencyDto currencyDtoOptional = currencyService.findByCode(code);
            resp.setStatus(HttpServletResponse.SC_OK);
            writeCurrencyJson(writer, currencyDtoOptional);

        } catch (BadRequestException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ResponseUtil.writeError(resp, e.getMessage());

        } catch (NotFoundException e){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ResponseUtil.writeError(resp, e.getMessage());

        } catch (DatabaseException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ResponseUtil.writeError(resp, "Ошибка базы данных");
        }
    }
}
