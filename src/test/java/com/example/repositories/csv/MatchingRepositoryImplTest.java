package com.example.repositories.csv;

import com.example.exceptions.ParseException;
import com.example.pojo.Matching;
import com.example.repositories.MatchingRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.example.TestUtil.streamToList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MatchingRepositoryImplTest {

    @Test
    public void shouldThrowExceptionOnNullInput() {
        assertThrows(IllegalStateException.class, () ->
                new MatchingRepositoryImpl(null)
        );
    }

    @Test
    public void shouldThrowExceptionOnEmptyInput() {
        assertThrows(IllegalStateException.class, () ->
                new MatchingRepositoryImpl("")
        );
    }

    @Test
    public void shouldThrowExceptionOnNotExistingFileInput() {
        assertThrows(ParseException.class, () ->
                new MatchingRepositoryImpl("dummy")
        );
    }

    @Test
    public void shouldThrowExceptionOnInvalidFileInput() {
        assertThrows(ParseException.class, () ->
                new MatchingRepositoryImpl("broken-matchings.csv")
        );
    }

    @Test
    public void shouldReturnMatchingForExistingId() {
        MatchingRepository matchingRepository = new MatchingRepositoryImpl("matchings.csv");

        Optional<Matching> expected = Optional.of(new Matching(1L, 2L));
        Optional<Matching> actual = matchingRepository.getMatchingById(1L);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldReturnOptionalNoneForUnknownId() {
        MatchingRepository matchingRepository = new MatchingRepositoryImpl("matchings.csv");

        Optional<Matching> expected = Optional.empty();
        Optional<Matching> actual = matchingRepository.getMatchingById(5L);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldReturnStreamWithAllElements() {
        MatchingRepository matchingRepository = new MatchingRepositoryImpl("matchings.csv");

        List<Matching> actual = streamToList(matchingRepository.findAll());

        assertThat(actual, hasSize(2));
        assertThat(actual, containsInAnyOrder(
                new Matching(1L, 2L),
                new Matching(2L, 3L)));
    }

    @Test
    public void shouldReturnEmptyStreamIfNoElementsPresent() {
        MatchingRepository matchingRepository = new MatchingRepositoryImpl("matchings-empty.csv");

        List<Matching> actual = streamToList(matchingRepository.findAll());

        assertThat(actual, is(empty()));
    }
}