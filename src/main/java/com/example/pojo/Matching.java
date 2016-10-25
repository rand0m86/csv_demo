package com.example.pojo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import static com.google.common.base.Preconditions.checkState;

/**
 * Immutable POJO representing matching entity.
 */
@Getter
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class Matching {
    long matchingId;
    long topPricedCount;

    public Matching(long matchingId, long topPricedCount) {
        this.matchingId = matchingId;
        checkState(topPricedCount >= 0, "Top priced count should be greater or equal to zero");
        this.topPricedCount = topPricedCount;
    }
}
