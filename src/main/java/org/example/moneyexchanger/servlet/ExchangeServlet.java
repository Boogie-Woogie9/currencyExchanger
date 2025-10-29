package org.example.moneyexchanger.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.moneyexchanger.model.ExchangeRate;
import org.example.moneyexchanger.service.ExchangeRateService;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@WebServlet(urlPatterns = {"/exchange"})
public class ExchangeServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;
    private ObjectMapper mapper = new ObjectMapper();

    public ExchangeServlet(){}

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.exchangeRateService = (ExchangeRateService) config.getServletContext().getAttribute("exchangeRateService");
        if (exchangeRateService == null) {
            throw new IllegalStateException("ExchangeRateService not found in ServletContext");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        if (from == null || to == null || amountStr == null) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters: from, to, amount");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);

            // Получаем курс с учётом всех 3 сценариев
            BigDecimal rate = exchangeRateService.getExchangeRate(from.toUpperCase(), to.toUpperCase());

            // Пересчитываем сумму
            BigDecimal converted = exchangeRateService.convertAmount(from.toUpperCase(), to.toUpperCase(), amount);

            Map<String, Object> result = Map.of(
                    "from", from.toUpperCase(),
                    "to", to.toUpperCase(),
                    "rate", rate.setScale(6),
                    "amount", amount.setScale(2),
                    "result", converted
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), result);

        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid amount format");
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }

    private void sendError(HttpServletResponse resp, int code, String message) throws IOException {
        resp.setStatus(code);
        mapper.writeValue(resp.getWriter(), Map.of("error", message));
    }
}
