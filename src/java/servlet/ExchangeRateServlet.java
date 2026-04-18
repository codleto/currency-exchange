package servlet;

import dto.ExchangeRateDto;
import exception.BadRequestException;
import exception.ConflictException;
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

        try {
            ExchangeRateDto byCode = exchangeRateService.findByCode(baseCode, targetCode);

            resp.setStatus(HttpServletResponse.SC_OK);
            JsonUtil.writeExchangeRateJson(writer, byCode);

        } catch (BadRequestException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ResponseUtil.writeError(resp, e.getMessage());

        } catch (NotFoundException e){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ResponseUtil.writeError(resp, e.getMessage());

        } catch (DatabaseException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ResponseUtil.writeError(resp, "Ошибка базы данных");
        }
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
            ResponseUtil.writeError(resp, "Данные заполнены не полностью");
            return;
        }

        try {
            ExchangeRateDto save = exchangeRateService.save(baseCode, targetCode, rateString);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.writeExchangeRateJson(writer,save);

        } catch (BadRequestException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ResponseUtil.writeError(resp, e.getMessage());

        } catch (NotFoundException e){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ResponseUtil.writeError(resp, e.getMessage());

        } catch (ConflictException e){
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            ResponseUtil.writeError(resp, e.getMessage());

        } catch (DatabaseException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ResponseUtil.writeError(resp, "Ошибка базы данных");
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String pathInfo = req.getPathInfo();
        String code = pathInfo.substring(1);

        String base = code.substring(0,3);
        String target = code.substring(3);
        String rateString = req.getParameter("rate");

        if(base.isBlank() || target.isBlank() || rateString == null || rateString.isBlank()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ResponseUtil.writeError(resp, "Данные заполнены не полностью");
            return;
        }

        try {
            exchangeRateService.update(base, target, rateString);
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (BadRequestException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ResponseUtil.writeError(resp, e.getMessage());

        } catch (NotFoundException e){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ResponseUtil.writeError(resp, e.getMessage());

        } catch (DatabaseException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ResponseUtil.writeError(resp, "Ошибка базы данных");
        }
    }
}
