package servlet;

import dto.CurrencyDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static util.JsonUtil.extracted;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String pathInfo = req.getPathInfo();
        String code = pathInfo.substring(1);

        PrintWriter writer = resp.getWriter();

        Optional<CurrencyDto>currencyDtoOptional = currencyService.findByCode(code);

        if(currencyDtoOptional.isEmpty()){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writer.write("{\"message\": \"База данных недоступна\"}");
            return;
        }

        CurrencyDto currencyDto = currencyDtoOptional.get();
        resp.setStatus(HttpServletResponse.SC_OK);

        extracted(writer, currencyDto);
    }

}
