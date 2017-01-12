package com.example.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.example.TestUtil.toRow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvRowTest {

    @Test
    public void shouldReturnEmptyRowOnNullableInput() {
        CsvRow row = new CsvRow(null);

        assertThat(row.isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnOptionalEmptyIfKeyIsNotPresent() {
        CsvRow emptyRow = toRow("");

        assertThat(emptyRow.get("key")).isEqualTo(Optional.empty());
    }

    @Test
    public void shouldReturnOptionalEmptyIfGivenParserButKeyIsNotPresent() {
        CsvRow emptyRow = toRow("");

        assertThat(emptyRow.get("age", Integer::parseInt)).isEqualTo(Optional.empty());
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

        assertThat(row.get("age", Integer::parseInt)).isEqualTo(Optional.of(25));
    }

    @Test
    public void shouldCompareRowsByValues() {
        CsvRow firstRow = toRow("age", "40");
        CsvRow secondRow = toRow("age", "40");

        assertThat(firstRow).isEqualTo(secondRow);
    }

    @Test
    public void shouldHaveSameHashCodesForEqualRows() {
        CsvRow firstRow = toRow("age", "40");
        CsvRow secondRow = toRow("age", "40");

        assertThat(firstRow.hashCode()).isEqualTo(secondRow.hashCode());
    }

}