package org.example.moneyexchanger.servlet;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.example.moneyexchanger.dao.CurrencyDao;
import org.example.moneyexchanger.dao.ExchangeRateDao;
import org.example.moneyexchanger.service.CurrencyService;
import org.example.moneyexchanger.service.ExchangeRateService;
import tools.jackson.databind.ObjectMapper;

public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        // === DAO ===
        CurrencyDao currencyDao = new CurrencyDao();
        ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
        // === SERVICE ===
        ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateDao, currencyDao);
        CurrencyService currencyService = new CurrencyService(currencyDao);
        // === CONTEXT ===
        sce.getServletContext().setAttribute("currencyService", currencyService);
        sce.getServletContext().setAttribute("exchangeRateService", exchangeRateService);

        System.out.println("✅ Application context initialized successfully!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }
}
