package com.example.repositories.csv;

import com.example.repositories.ProductRepository;
import com.example.exceptions.ParseException;
import com.example.pojo.Product;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.example.TestUtil.streamToList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductRepositoryImplTest {

    @Test
    public void shouldThrowExceptionOnNullInput() {
        assertThrows(IllegalStateException.class, () ->
                new ProductRepositoryImpl(null)
        );
    }

    @Test
    public void shouldThrowExceptionOnEmptyInput() {
        assertThrows(IllegalStateException.class, () ->
                new ProductRepositoryImpl("")
        );
    }

    @Test
    public void shouldThrowExceptionOnNotExistingFileInput() {
        assertThrows(ParseException.class, () ->
                new ProductRepositoryImpl("dummy")
        );
    }

    @Test
    public void shouldThrowExceptionOnInvalidFileInput() {
        assertThrows(ParseException.class, () ->
                new ProductRepositoryImpl("broken-currencies.csv")
        );
    }

    @Test
    public void shouldReturnStreamWithAllElementsForMatchingId() {
        ProductRepository productRepository = new ProductRepositoryImpl("products.csv");

        List<Product> actual = streamToList(productRepository.findProductsByMatchingId(2L));

        assertThat(actual).hasSize(2);
        assertThat(actual).containsExactlyInAnyOrder(
                new Product(1, 1000D, "GBP", 2, 2L),
                new Product(2, 1050D, "EU", 1, 2L));
    }

    @Test
    public void shouldReturnEmptyStreamForMissingMatchingId() {
        ProductRepository productRepository = new ProductRepositoryImpl("products.csv");

        List<Product> actual = streamToList(productRepository.findProductsByMatchingId(999L));

        assertThat(actual).isEmpty();
    }

    @Test
    public void shouldReturnStreamWithAllElements() {
        ProductRepository productRepository = new ProductRepositoryImpl("products.csv");

        List<Product> actual = streamToList(productRepository.findAll());

        assertThat(actual).hasSize(2);
        assertThat(actual).containsExactlyInAnyOrder(
                new Product(1, 1000D, "GBP", 2, 2L),
                new Product(2, 1050D, "EU", 1, 2L));
    }

    @Test
    public void shouldReturnEmptyStreamIfNoElementsPresent() {
        ProductRepository productRepository = new ProductRepositoryImpl("products-empty.csv");

        List<Product> actual = streamToList(productRepository.findAll());

        assertThat(actual).isEmpty();
    }

}