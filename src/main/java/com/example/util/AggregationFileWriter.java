package com.example.util;

import com.example.exceptions.FileWriteException;
import com.example.pojo.AggregationResult;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * File writer that writes file to folder from which application is started.
 */
public class AggregationFileWriter {
    private static final Logger logger = LoggerFactory.getLogger(AggregationFileWriter.class);

    public static AggregationFileWriter INSTANCE = new AggregationFileWriter();

    private AggregationFileWriter() {
    }

    public void write(Stream<AggregationResult> aggregationStream) {
        write(aggregationStream, "top_products.csv");
    }

    public void write(Stream<AggregationResult> aggregationStream, String outputFileName) {
        StringBuilder sb = new StringBuilder();
        Joiner joiner = Joiner.on(",");
        joiner.appendTo(sb, Arrays.asList("matching_id", "total_price", "avg_price", "currency", "ignored_products_count"));

        sb.append(System.lineSeparator());

        aggregationStream.forEach(r -> {
                joiner.appendTo(sb,
                    Arrays.asList(
                        r.getMatchingId(),
                        doubleToStringWithLimitedPrecision(r.getTotalPrice()),
                        doubleToStringWithLimitedPrecision(r.getAvgPrice()),
                        r.getCurrency(),
                        r.getIgnoredProductsCount())
                );
                sb.append(System.lineSeparator());
            }
        );

        writeToFile(outputFileName, sb);
    }

    private String doubleToStringWithLimitedPrecision(double value) {
        return String.format("%.3f", value);
    }

    private void writeToFile(String fileName, StringBuilder contents) {
        logger.debug("Writing to file '{}'", fileName);
        try {
            Files.write(
                    Paths.get(new File(fileName).toURI()),
                    contents.toString().getBytes(Charsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException ex) {
            logger.error("Can't write file", ex);
            throw new FileWriteException(ex);
        }
        logger.debug("File written");
    }

}
