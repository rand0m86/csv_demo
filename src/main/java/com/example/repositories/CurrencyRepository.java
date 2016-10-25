package com.example.repositories;

import java.util.Optional;

/**
 * Currency repository. Contains operations available for currencies retrieval.
 */
public interface CurrencyRepository {

    /**
     * Returns ratio for given currency or empty {@code Optional} if currency
     * is not found.
     *
     * @param currency The currency name
     */
    Optional<Double> getRatioForCurrency(String currency);
}
