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
    private final ObjectMapper mapper;

    public ExchangeServlet(ExchangeRateService service, ObjectMapper mapper) {
        this.exchangeRateService = service;
        this.mapper = mapper;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.exchangeRateService = (ExchangeRateService) getServletContext().getAttribute("exchangeRateService");
        if (exchangeRateService == null) {
            throw new IllegalStateException("ExchangeRateService not found in ServletContext");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        if (from == null || to == null || amountStr == null) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters: from, to, amount");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);

            Optional<ExchangeRate> rateOpt = exchangeRateService.getExchangeRateByCodes(from.toUpperCase(), to.toUpperCase());

            if (rateOpt.isEmpty()) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Exchange rate not found for " + from + " -> " + to);
                return;
            }

            ExchangeRate rate = rateOpt.get();
            BigDecimal convertedAmount = amount.multiply(rate.getRate());

            Map<String, Object> result = Map.of(
                    "baseCurrency", from.toUpperCase(),
                    "targetCurrency", to.toUpperCase(),
                    "rate", rate.getRate(),
                    "amount", amount,
                    "convertedAmount", convertedAmount
            );

            mapper.writeValue(resp.getWriter(), result);
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid amount format");
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

//    // ==== POST /exchange (альтернатива GET, если хочешь form-data) ====
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        doGet(req, resp); // просто переиспользуем логику
//    }

    private void sendError(HttpServletResponse resp, int code, String message) throws IOException {
        resp.setStatus(code);
        mapper.writeValue(resp.getWriter(), Map.of("error", message));
    }
}
