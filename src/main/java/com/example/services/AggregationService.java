package com.example.services;

import com.example.repositories.CurrencyRepository;
import com.example.repositories.MatchingRepository;
import com.example.repositories.ProductRepository;
import com.example.exceptions.ProcessingException;
import com.example.pojo.AggregationResult;
import com.example.pojo.Matching;
import com.example.pojo.Product;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service handling all aggregation logic.
 */
public class AggregationService {
    private static final Logger logger = LoggerFactory.getLogger(AggregationService.class);

    private static final Comparator<IntermediateResult> DESCENDING_ORDER_PRICE_COMPARATOR =
            Comparator.comparingDouble(IntermediateResult::getTotalPriceConverted)
                .reversed();

    private final ProductRepository productRepository;
    private final MatchingRepository matchingRepository;
    private final CurrencyRepository currencyRepository;

    public AggregationService(
            ProductRepository productRepository,
            MatchingRepository matchingRepository,
            CurrencyRepository currencyRepository) {

        this.productRepository = productRepository;
        this.matchingRepository = matchingRepository;
        this.currencyRepository = currencyRepository;
    }

    /**
     * Returns aggregated data for the values provided in the repositories.
     */
    public Stream<AggregationResult> aggregateProducts() {
        logger.debug("Started processing of given data");
        try {
            return matchingRepository.findAll()
                    .map(this::resultsForMatchingProducts)
                    .flatMap(Function.identity())
                    .map(this::toAggregation);
        } finally {
            logger.debug("Data processing completed");
        }
    }

    private AggregationResult toAggregation(IntermediateResult intermediateResult) {
        return new AggregationResult(
                intermediateResult.getMatchingId(),
                intermediateResult.getTotalPrice(),
                intermediateResult.getAvgPrice(),
                intermediateResult.getCurrency(),
                intermediateResult.getIgnoredProductsCount()
        );
    }

    private Stream<IntermediateResult> resultsForMatchingProducts(Matching matching) {
        List<IntermediateResult> resultsForMatching = productRepository.findAll()
                .filter(p ->
                        matching.getMatchingId() == p.getMatchingId()
                )
                .map(product -> getProductStatsByMatching(matching, product))
                .sorted(DESCENDING_ORDER_PRICE_COMPARATOR)
                .limit(matching.getTopPricedCount())
                .collect(Collectors.toList());

        double priceAverage = totalPriceAverage(resultsForMatching);

        return addAverageToResults(resultsForMatching.stream(), priceAverage);
    }

    private IntermediateResult getProductStatsByMatching(Matching matching, Product product) {
        double totalPrice = getTotalPrice(product);
        double currencyRatio = getRatioForCurrency(currencyRepository, product.getCurrency());
        double totalPriceWithRatio = applyRatio(totalPrice, currencyRatio);
        long totalProductsByMatching = totalProductsCountByMatching(productRepository, matching);
        long ignoredCount = getIgnoredCount(totalProductsByMatching, matching.getTopPricedCount());

        return new IntermediateResult(
                matching.getMatchingId(),
                product.getQuantity(),
                totalPrice,
                0,
                currencyRatio,
                totalPriceWithRatio,
                product.getCurrency(),
                ignoredCount
        );
    }

    private double totalPriceAverage(Collection<IntermediateResult> intermediateResults) {
        return intermediateResults.stream()
                .mapToDouble(r ->
                        r.getTotalPriceConverted() / r.getQuantity()
                )
                .average()
                .orElse(0);
    }

    private Stream<IntermediateResult> addAverageToResults(Stream<IntermediateResult> results, double priceAverage) {
        return results.map(r -> new IntermediateResult(
                r.getMatchingId(),
                r.getQuantity(),
                r.getTotalPrice(),
                getAveragePriceWithRatio(priceAverage, r.getCurrencyRatio()),
                r.getCurrencyRatio(),
                r.getTotalPriceConverted(),
                r.getCurrency(),
                r.getIgnoredProductsCount())
        );
    }

    private double getAveragePriceWithRatio(double priceAverage, double ratio) {
        return priceAverage / ratio;
    }

    private double getTotalPrice(Product product) {
        return product.getQuantity() * product.getPrice();
    }

    private double getRatioForCurrency(CurrencyRepository currencyRepository, String currency) {
        return currencyRepository.getRatioForCurrency(currency)
                .orElseThrow(
                        () -> new ProcessingException(String.format("Can't find currency '%s'", currency)));
    }

    private double applyRatio(double amount, double ratio) {
        return amount * ratio;
    }

    private long totalProductsCountByMatching(ProductRepository productRepository, Matching matching) {
        return productRepository.findProductsByMatchingId(matching.getMatchingId())
                .count();
    }

    private long getIgnoredCount(long total, long limit) {
        if (limit > total) {
            return 0;
        }
        return total - limit;
    }

    /**
     * POJO representing intermediate calculation result.
     * Created because of limitation of Java not having tuples.
     */
    @Value
    private static class IntermediateResult {
        long matchingId;
        int quantity;
        double totalPrice;
        double avgPrice;
        double currencyRatio;
        double totalPriceConverted;
        String currency;
        long ignoredProductsCount;
    }

}
