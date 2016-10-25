package com.example.repositories.csv;

import com.example.repositories.CurrencyRepository;
import com.example.pojo.Currency;
import com.example.util.CsvRow;

import java.util.Optional;

/**
 * Currency repository implementation for database given as CSV file.
 *
 * @see CurrencyRepository
 */
public class CurrencyRepositoryImpl extends AbstractRepositoryImpl<Currency> implements CurrencyRepository {

    public CurrencyRepositoryImpl(String currenciesFile) {
        super(currenciesFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Currency toEntity(CsvRow row) {
        Optional<String> currency = row.get("currency");
        return new Currency(
                currency.get(),
                row.get("ratio", Double::valueOf).get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Double> getRatioForCurrency(String currency) {
        return getRepository()
                .filter(c -> c.getName().equalsIgnoreCase(currency))
                .map(Currency::getRatio)
                .findFirst();
    }
}
