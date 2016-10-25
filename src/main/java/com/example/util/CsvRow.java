package com.example.util;

import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;

/**
 * Class representing parsed row of CSV file.
 */
@ToString
public class CsvRow {
    private final Map<String, String> row;

    public CsvRow(Map<String, String> row) {
        if (null == row) {
            row = new HashMap<>();
        }
        this.row = row;
    }

    /**
     * Returns {@code Optional} of value for the given cell name.
     *
     * {@code Optional.empty()} is returned if cell name not found in the current
     * row.
     *
     * @param name The name of the cell to look in the row
     * @return Optional of value for given cell name
     */
    public Optional<String> get(String name) {
        return Optional.ofNullable(row.get(name));
    }

    /**
     * Returns {@code Optional} of value for the given cell name.
     *
     * {@code Optional.empty()} is returned if cell name not found in the current
     * row.
     *
     * @param name The name of the cell to look in the row
     * @param converter The converter from String to given type
     * @param <T> Type of value that is retrieved from cell
     * @return Optional of value for given key
     */
    public <T> Optional<T> get(String name, Function<String, T> converter) {
        checkState(converter != null, "Parser should not be null");
        return get(name).map(converter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CsvRow csvRow = (CsvRow) o;

        return row.equals(csvRow.row);
    }

    /**
     * Returns {@code true} if there are no cells in the given row and
     * {@code false} otherwise.
     */
    public boolean isEmpty() {
        return row.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return row.hashCode();
    }

}
