package com.example.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.example.TestUtil.toRow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvRowTest {

    @Test
    public void shouldReturnEmptyRowOnNullableInput() {
        CsvRow row = new CsvRow(null);

        assertThat(row.isEmpty(), is(true));
    }

    @Test
    public void shouldReturnOptionalEmptyIfKeyIsNotPresent() {
        CsvRow emptyRow = toRow("");

        assertThat(emptyRow.get("key"), is(Optional.empty()));
    }

    @Test
    public void shouldReturnOptionalEmptyIfGivenParserButKeyIsNotPresent() {
        CsvRow emptyRow = toRow("");

        assertThat(emptyRow.get("age", Integer::parseInt), is(Optional.empty()));
    }

    @Test
    public void shouldThrowExceptionIfConverterIsNull() {
        CsvRow emptyRow = toRow("age", "25");

        assertThrows(IllegalStateException.class, () ->
                emptyRow.get("age", null)
        );
    }

    @Test
    public void shouldBeAbleToParseValueWithGivenFunction() {
        CsvRow row = toRow("age", "25");

        assertThat(row.get("age", Integer::parseInt), is(Optional.of(25)));
    }

    @Test
    public void shouldCompareRowsByValues() {
        CsvRow firstRow = toRow("age", "40");
        CsvRow secondRow = toRow("age", "40");

        assertThat(firstRow, is(equalTo(secondRow)));
    }

    @Test
    public void shouldHaveSameHashCodesForEqualRows() {
        CsvRow firstRow = toRow("age", "40");
        CsvRow secondRow = toRow("age", "40");

        assertThat(firstRow.hashCode(), is(equalTo(secondRow.hashCode())));
    }

}