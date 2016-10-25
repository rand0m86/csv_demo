package com.example.util;

import com.example.exceptions.FileNotFoundException;
import com.example.exceptions.ParseException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.TestUtil.toRow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvFileReaderTest {

    @Test
    public void shouldThrowFileNotFoundExceptionIfFileDoesNotExist() {
        assertThrows(FileNotFoundException.class, () ->
                readFile("dummy")
        );
    }

    @Test
    public void shouldThrowFileNotFoundExceptionIfPathIsNull() {
        assertThrows(FileNotFoundException.class, () ->
                readFile(null)
        );
    }

    @Test
    public void shouldReturnEmptyCollectionOnEmptyFile() {
        List<CsvRow> rows = readFile("empty.csv");

        assertThat(rows, is(empty()));
    }

    @Test
    public void shouldReturnEmptyCollectionIfFileContainsOnlyHeader() {
        List<CsvRow> rows = readFile("only-header.csv");

        assertThat(rows, is(empty()));
    }

    @Test
    public void shouldReadFileFromClasspath() {
        List<CsvRow> rows = readFile("comma-separated.csv");

        assertThat(rows, is(not(empty())));
    }

    @Test
    public void shouldThrowExceptionIfHeaderAndRowHaveDifferentSize() {
        assertThrows(ParseException.class, () ->
            readFile("malformed-elements-per-row.csv")
        );
    }

    @Test
    public void shouldParseFileAsCommaSeparatedByDefault() {
        List<CsvRow> rows = readFile("comma-separated.csv");

        assertThat(rows, hasSize(2));

        assertThat(rows.get(0), is(toRow("name", "Jack", "age", "25")));
        assertThat(rows.get(1), is(toRow("name", "Sam", "age", "42")));
    }

    @Test
    public void shouldTrimCellsValuesForCsvFile() {
        List<CsvRow> rows = readFile("comma-separated-with-spaces.csv");

        assertThat(rows, hasSize(2));

        assertThat(rows.get(0), is(toRow("name", "Jack", "age", "25")));
        assertThat(rows.get(1), is(toRow("name", "Sam", "age", "42")));
    }

    @Test
    public void shouldBeAbleToReadFileWithCustomSeparator() {
        List<CsvRow> rows = streamToList(
                CsvFileReader.INSTANCE.readFile("pipe-separated.txt", "|")
        );

        assertThat(rows, hasSize(2));

        assertThat(rows.get(0), is(toRow("name", "phone", "price", "2500")));
        assertThat(rows.get(1), is(toRow("name", "TV", "price", "4500")));
    }

    @Test
    public void shouldBeAbleToReadSpaceDelimitedFile() {
        List<CsvRow> rows = streamToList(
                CsvFileReader.INSTANCE.readFile("space-separated.txt", " ")
        );

        assertThat(rows, hasSize(2));
        assertThat(rows.get(0), is(toRow("country", "Ukraine", "capital", "Kyiv")));
        assertThat(rows.get(1), is(toRow("country", "Poland", "capital", "Warsaw")));
    }

    private List<CsvRow> readFile(String name) {
        return streamToList(
                CsvFileReader.INSTANCE.readFile(name)
        );
    }

    private List<CsvRow> streamToList(Stream<CsvRow> stream) {
        return stream.collect(Collectors.toList());
    }
}