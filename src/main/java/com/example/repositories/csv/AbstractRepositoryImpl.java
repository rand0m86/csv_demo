package com.example.repositories.csv;

import com.example.exceptions.ParseException;
import com.example.util.CsvFileReader;
import com.example.util.CsvRow;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;

/**
 * Abstract class containing default implementation for common methods of repositories.
 * @param <T> Type of repository entity
 */
abstract class AbstractRepositoryImpl<T> {

    private final Supplier<Stream<T>> repository;

    AbstractRepositoryImpl(String csvFileName) {
        checkState(StringUtils.isNotEmpty(csvFileName), "CSV file name should be present");
        repository = fileToEntity(csvFileName, this::toEntity);
    }

    /**
     * Converts given file to supplier of Stream containing elements of repository type.
     *
     * @param fileName The CSV file name
     * @param converter CsvRow to repository type converter
     * @return Supplier of Stream of entities
     */
    protected Supplier<Stream<T>> fileToEntity(String fileName, Function<CsvRow, T> converter) {
        try {
            List<T> asList = CsvFileReader.INSTANCE.readFile(fileName)
                    .map(converter)
                    .collect(Collectors.toList());
            return asList::stream;
        } catch (Exception ex) {
            throw new ParseException(ex);
        }
    }

    /**
     * Returns stream of entities from internal supplier.
     *
     * @return Stream of entities
     */
    Stream<T> getRepository() {
        return repository.get();
    }

    /**
     * Returns converter from CSV row to entity.
     *
     * Throws {@code RuntimeException} if converting failed due to any reason.
     *
     * @param row The CSV row
     * @return Converter of row
     *
     * @throws RuntimeException
     */
    protected abstract T toEntity(CsvRow row);
}
