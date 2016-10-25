package com.example;

import com.example.util.CsvRow;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtil {

    private TestUtil() {
    }

    public static CsvRow toRow(String... keyValues) {
        HashMap<String, String> row = new HashMap<>();
        for (int i = 0; i < keyValues.length - 1; i = i + 2) {
            row.put(keyValues[i], keyValues[i + 1]);
        }
        return new CsvRow(row);
    }

    public static <T> List<T> streamToList(Stream<T> stream) {
        return stream.collect(Collectors.toList());
    }
}
