package com.example.util;

import com.example.exceptions.FileNotFoundException;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Utility class that can read files from classpath.
 */
public class ClasspathFileReader {

    public static ClasspathFileReader INSTANCE = new ClasspathFileReader();

    private ClasspathFileReader() {
    }

    /**
     * Returns a stream containing of lines of the classpath file.
     *
     * @param name The name of classpath resource
     * @return Stream of lines
     *
     * @throws FileNotFoundException
     *         If {@code name} is {@code null} or file is not
     *         resolvable via classpath
     */
    public Stream<String> readFile(String name) {
        try {
            URI uri = ClassLoader.getSystemResource(name).toURI();
            return Files.lines(Paths.get(uri));
        } catch (Exception ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

}
