package com.example.util;

import com.example.exceptions.FileNotFoundException;
import com.example.exceptions.ParseException;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class that loads given resource from classpath and splits
 * each line by given delimiter.
 */
public class CsvFileReader {

    public static final CsvFileReader INSTANCE = new CsvFileReader();

    private CsvFileReader() {
    }

    /**
     * Returns a stream of rows for a given comma-separated file.
     *
     * @param name The name of the comma-separated file
     * @return Stream of rows
     *
     * @throws FileNotFoundException
     *         If {@code name} is {@code null} or file can't be found
     *         in the classpath
     */
    public Stream<CsvRow> readFile(String name) {
        return readFile(name, ",");
    }

    /**
     * Returns a stream of rows for a given comma-separated file.
     *
     * Lines are represented as a lists of String values.
     *
     * @param name The name of the delimiter-separated file
     * @param delimiter The String representing delimiter
     * @return Stream of rows
     *
     * @throws FileNotFoundException
     *         If {@code name} is {@code null} or file can't be found
     *         in the classpath
     */
    public Stream<CsvRow> readFile(String name, String delimiter) {
        List<List<String>> rows = ClasspathFileReader.INSTANCE.readFile(name)
                .map(s -> Splitter.on(delimiter).trimResults().splitToList(s))
                .collect(Collectors.toList());

        if (rows.isEmpty()) {
            return Stream.empty();
        }

        return handleRows(rows).stream();
    }

    private List<CsvRow> handleRows(List<List<String>> rows) {
        List<String> header = new ArrayList<>();
        List<CsvRow> result = new ArrayList<>(rows.size() - 1);
        for (int i = 0; i < rows.size(); i++) {
            if (i == 0) {
                header = rows.get(0);
            } else {
                result.add(toRow(header, rows.get(i)));
            }
        }
        return result;
    }

    private CsvRow toRow(List<String> header, List<String> values) {
        if (header.size() != values.size()) {
            throw new ParseException("Rows size is different");
        }
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (int i = 0; i < header.size(); i++) {
            builder.put(header.get(i), values.get(i));
        }
        return new CsvRow(builder.build());
    }
}
