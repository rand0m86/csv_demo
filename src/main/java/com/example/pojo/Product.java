package com.example.pojo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Immutable POJO representing product.
 */
@Getter
@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class Product {

    public Product(long id, double price, String currency, int quantity, long matchingId) {
        this.id = id;
        checkState(price >= 0, "Price should be greater than or equal to zero");
        this.price = price;
        this.currency = checkNotNull(currency);
        checkState(quantity >= 0, "Quantity should be greater or equal to zero");
        this.quantity = quantity;
        this.matchingId = matchingId;
    }

    long id;
    double price;
    String currency;
    int quantity;
    long matchingId;
}
