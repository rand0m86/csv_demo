package com.example.util;

import com.example.exceptions.FileNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClasspathFileReaderTest {

    @Test
    public void shouldThrowFileNotFoundExceptionIfFileDoesNotExist() {
        assertThrows(FileNotFoundException.class, () ->
                ClasspathFileReader.INSTANCE.readFile("dummy")
        );
    }

    @Test
    public void shouldThrowFileNotFoundExceptionIfPathIsNull() {
        assertThrows(FileNotFoundException.class, () ->
                ClasspathFileReader.INSTANCE.readFile(null)
        );
    }

    @Test
    public void shouldBeAbleToReadFilesByPath() {
        Stream<String> lines = ClasspathFileReader.INSTANCE.readFile("test-file.txt");
        assertThat(lines, is(notNullValue()));
    }

    @Test
    public void shouldReadExistingFile() {
        Stream<String> linesStream = ClasspathFileReader.INSTANCE.readFile("test-file.txt");
        List<String> lines = linesStream.collect(Collectors.toList());
        assertThat(lines, hasSize(2));
        assertThat(lines, contains("hello world", "bye bye world"));
    }

}
