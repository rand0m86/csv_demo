package com.example.pojo;

import lombok.NonNull;
import lombok.Value;

/**
 * Immutable POJO representing currency.
 */
@Value public class Currency {
    @NonNull String name;
    double ratio;
}
