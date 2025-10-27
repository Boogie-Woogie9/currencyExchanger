package org.example.moneyexchanger.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.moneyexchanger.model.Currency;
import org.example.moneyexchanger.service.CurrencyService;
import tools.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/currencies", "/currency/*"})
public class CurrencyServlet extends HttpServlet {

    private CurrencyService currencyService;
    private ObjectMapper mapper = new ObjectMapper();

    public CurrencyServlet(){}

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.currencyService = (CurrencyService) config.getServletContext().getAttribute("currencyService");
        if (this.currencyService == null){
            throw new IllegalStateException("CurrencyService not found in ServletContext");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        if (req.getServletPath().equals("/currencies")){
            List<Currency> currencies = currencyService.getAllCurrencies();
            resp.getWriter().write(mapper.writeValueAsString(currencies));
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (req.getServletPath().equals("/currency") && pathInfo != null && pathInfo.length() > 1){
            String code  = pathInfo.substring(1).toUpperCase();
            Currency currency = currencyService.getCurrencyByCode(code);

            if (currency == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Currency with code '" + code + "' not found.");
                return;
            }

            resp.getWriter().write(mapper.writeValueAsString(currency));
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.getServletPath().equals("/currencies")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Post only allowed on /currencies");
            return;
        }

        BufferedReader reader = req.getReader();
        Currency currency = mapper.readValue(reader, Currency.class);

        try {
            currencyService.createCurrency(currency);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(mapper.writeValueAsString(currency));
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
