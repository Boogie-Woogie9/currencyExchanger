package org.example.moneyexchanger.servlet;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.moneyexchanger.dao.CurrencyDao;
import org.example.moneyexchanger.dao.Database;
import org.example.moneyexchanger.dao.ExchangeRateDao;
import org.example.moneyexchanger.service.CurrencyService;
import org.example.moneyexchanger.service.ExchangeRateService;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        // === DATABASE ===
        Database.init(sce.getServletContext());
        // === DAO ===
        CurrencyDao currencyDao = new CurrencyDao();
        ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
        // === SERVICE ===
        ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateDao, currencyDao);
        CurrencyService currencyService = new CurrencyService(currencyDao);
        // === CONTEXT ===
        sce.getServletContext().setAttribute("currencyService", currencyService);
        sce.getServletContext().setAttribute("exchangeRateService", exchangeRateService);

        System.out.println("[OK]: Application context initialized successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[OK]: Application context destroyed.");
    }
}
