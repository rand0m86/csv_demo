package com.example;

import com.example.repositories.CurrencyRepository;
import com.example.repositories.MatchingRepository;
import com.example.repositories.ProductRepository;
import com.example.repositories.csv.CurrencyRepositoryImpl;
import com.example.repositories.csv.MatchingRepositoryImpl;
import com.example.repositories.csv.ProductRepositoryImpl;
import com.example.services.AggregationService;
import com.example.util.AggregationFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application launcher for the given task
 */
public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.debug("Application started");
        new App().run();
        logger.debug("Application finished");
    }

    private void run() {
        try {
            ProductRepository productRepository = new ProductRepositoryImpl("data.csv");
            CurrencyRepository currencyRepository = new CurrencyRepositoryImpl("currencies.csv");
            MatchingRepository matchingRepository = new MatchingRepositoryImpl("matchings.csv");

            AggregationService service = new AggregationService(productRepository, matchingRepository, currencyRepository);
            AggregationFileWriter.INSTANCE.write(service.aggregateProducts());
        } catch (Exception ex) {
            logger.error("Error happened during application execution", ex);
        }

    }

}
