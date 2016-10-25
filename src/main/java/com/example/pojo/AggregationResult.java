package com.example.pojo;

import lombok.NonNull;
import lombok.Value;

/**
 * Immutable POJO representing aggregation result.
 */
@Value public class AggregationResult {
    long matchingId;
    double totalPrice;
    double avgPrice;
    @NonNull String currency;
    long ignoredProductsCount;
}
