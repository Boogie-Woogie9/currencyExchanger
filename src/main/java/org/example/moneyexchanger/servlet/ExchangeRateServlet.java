package org.example.moneyexchanger.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.moneyexchanger.dto.ExchangeRateDto;
import org.example.moneyexchanger.model.ExchangeRate;
import org.example.moneyexchanger.service.ExchangeRateService;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/exchangeRates", "/exchangeRate/*"})
public class ExchangeRateServlet extends HttpServlet {

    private ExchangeRateService service;
    private final ObjectMapper mapper;

    public ExchangeRateServlet(ExchangeRateService service, ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.service = (ExchangeRateService) getServletContext().getAttribute("ExchangeRateService");
        if (service == null) {
            throw new IllegalStateException("ExchangeRateService not found in ServletContext!");
        }
    }

    // ===== GET /exchangeRates  и  /exchangeRate/USDRUB =====
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");

        String path = req.getPathInfo();

        try {
            if (path == null || path.equals("/")) {
                // Получить все курсы
                List<ExchangeRate> list = service.getAllExchangeRates();
                mapper.writeValue(resp.getWriter(), list);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                // Получить курс по валютной паре
                String pair = path.substring(1).toUpperCase(); // например USDRUB
                if (pair.length() != 6) {
                    sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid currency pair format");
                    return;
                }

                String base = pair.substring(0, 3);
                String target = pair.substring(3, 6);

                Optional<ExchangeRate> rate = service.getExchangeRateByCodes(base, target);

                if (rate.isEmpty()) {
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Exchange rate not found");
                } else {
                    mapper.writeValue(resp.getWriter(), rate.get());
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // ===== POST /exchangeRates =====
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");

        String baseCode = req.getParameter("baseCurrencyCode");
        String targetCode = req.getParameter("targetCurrencyCode");
        String rateStr = req.getParameter("rate");

        if (baseCode == null || targetCode == null || rateStr == null) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing required form fields");
            return;
        }

        try {
            BigDecimal rate = new BigDecimal(rateStr);
            ExchangeRateDto dto = new ExchangeRateDto(baseCode, targetCode, rate);

            service.createExchangeRate(dto);

            // Возвращаем созданный объект
            Optional<ExchangeRate> created = service.getExchangeRateByCodes(baseCode, targetCode);
            mapper.writeValue(resp.getWriter(), created.get());
            resp.setStatus(HttpServletResponse.SC_CREATED);

        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // ===== PATCH /exchangeRate/USDRUB =====
    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");

        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing currency pair");
            return;
        }

        String pair = path.substring(1).toUpperCase();
        if (pair.length() != 6) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid currency pair format");
            return;
        }

        String rateStr = req.getParameter("rate");
        if (rateStr == null) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing rate parameter");
            return;
        }

        try {
            BigDecimal rate = new BigDecimal(rateStr);
            String base = pair.substring(0, 3);
            String target = pair.substring(3, 6);

            Optional<ExchangeRate> existing = service.getExchangeRateByCodes(base, target);
            if (existing.isEmpty()) {
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Exchange rate not found");
                return;
            }

            ExchangeRate rateEntity = existing.get();
            rateEntity.setRate(rate);
            service.updateExchangeRate(rateEntity);

            mapper.writeValue(resp.getWriter(), rateEntity);
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid rate format");
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // ===== helper =====
    private void sendError(HttpServletResponse resp, int code, String message) throws IOException {
        resp.setStatus(code);
        mapper.writeValue(resp.getWriter(), java.util.Map.of("message", message));
    }
}
