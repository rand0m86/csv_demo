package com.example.repositories.csv;

import com.example.exceptions.ParseException;
import com.example.repositories.CurrencyRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CurrencyRepositoryImplTest {

    @Test
    public void shouldThrowExceptionOnNullInput() {
        assertThrows(IllegalStateException.class, () ->
                new CurrencyRepositoryImpl(null)
        );
    }

    @Test
    public void shouldThrowExceptionOnEmptyInput() {
        assertThrows(IllegalStateException.class, () ->
                new CurrencyRepositoryImpl("")
        );
    }

    @Test
    public void shouldThrowExceptionOnNotExistingFileInput() {
        assertThrows(ParseException.class, () ->
                new CurrencyRepositoryImpl("dummy")
        );
    }

    @Test
    public void shouldThrowExceptionOnInvalidFileInput() {
        assertThrows(ParseException.class, () ->
                new CurrencyRepositoryImpl("broken-currencies.csv")
        );
    }

    @Test
    public void shouldReturnRatioForKnownCurrency() {
        CurrencyRepository currencyRepository = new CurrencyRepositoryImpl("currencies.csv");

        Optional<Double> expected = Optional.of(2.4);
        Optional<Double> actual = currencyRepository.getRatioForCurrency("GBP");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldReturnOptionalNoneForUnknownCurrency() {
        CurrencyRepository currencyRepository = new CurrencyRepositoryImpl("currencies.csv");

        Optional<Double> expected = Optional.empty();
        Optional<Double> actual = currencyRepository.getRatioForCurrency("WMZ");

        assertThat(actual).isEqualTo(expected);
    }

}