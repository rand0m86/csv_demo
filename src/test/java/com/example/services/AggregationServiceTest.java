package com.example.services;

import com.example.exceptions.ProcessingException;
import com.example.pojo.AggregationResult;
import com.example.pojo.Matching;
import com.example.pojo.Product;
import com.example.repositories.CurrencyRepository;
import com.example.repositories.MatchingRepository;
import com.example.repositories.ProductRepository;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.example.TestUtil.streamToList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class AggregationServiceTest {

    private ProductRepository productRepositoryMock = mock(ProductRepository.class);
    private MatchingRepository matchingRepositoryMock = mock(MatchingRepository.class);
    private CurrencyRepository currencyRepositoryMock = mock(CurrencyRepository.class);

    private AggregationService aggregationService;

    @BeforeEach
    public void setUp() {
        aggregationService = new AggregationService(
                productRepositoryMock,
                matchingRepositoryMock,
                currencyRepositoryMock
        );
    }

    @AfterEach
    public void tearDown() {
        reset(productRepositoryMock);
        reset(matchingRepositoryMock);
        reset(currencyRepositoryMock);
    }

    @Test
    public void shouldReturnEmptyResultOnEmptyMatchings() {
        when(matchingRepositoryMock.findAll()).thenReturn(Stream.empty());

        List<AggregationResult> actual = streamToList(aggregationService.aggregateProducts());

        assertThat(actual, is(empty()));
    }

    @Test
    public void shouldReturnEmptyResultOnEmptyProducts() {
        when(matchingRepositoryMock.findAll()).thenReturn(Stream.empty());
        when(productRepositoryMock.findAll()).thenReturn(Stream.empty());

        List<AggregationResult> actual = streamToList(aggregationService.aggregateProducts());

        assertThat(actual, is(empty()));
    }

    @Test
    public void shouldReturnEmptyResultOnMissingProductsWithGivenMatchingId() {
        when(matchingRepositoryMock.findAll()).thenReturn(Stream.of(new Matching(1L, 1L)));
        when(productRepositoryMock.findAll()).thenReturn(Stream.of(new Product(1, 10D, "USD", 1, 2L)));

        List<AggregationResult> actual = streamToList(aggregationService.aggregateProducts());

        assertThat(actual, is(empty()));
    }

    @Test
    public void shouldThrowExceptionIfCurrencyNotFoundInsideCurrencyRepository() {
        when(matchingRepositoryMock.findAll()).thenReturn(Stream.of(new Matching(1L, 0L)));
        when(productRepositoryMock.findAll()).thenReturn(Stream.of(new Product(1, 10D, "USD", 1, 1L)));
        when(currencyRepositoryMock.getRatioForCurrency("USD")).thenReturn(Optional.empty());

        ProcessingException processingException = expectThrows(ProcessingException.class, () ->
                streamToList(aggregationService.aggregateProducts())
        );

        assertThat("Can't find currency 'USD'", is(processingException.getMessage()));
    }

    @Test
    public void shouldReturnEmptyResultOnZeroTopPricedCountForGivenMatching() {
        when(matchingRepositoryMock.findAll()).thenReturn(Stream.of(new Matching(1L, 0L)));
        when(productRepositoryMock.findAll()).thenReturn(Stream.of(new Product(1, 10D, "USD", 1, 1L)));
        initCurrencyMockWith(ImmutableMap.of("USD", 1D));

        List<AggregationResult> actual = streamToList(aggregationService.aggregateProducts());

        assertThat(actual, is(empty()));
    }

    @Test
    public void shouldProcessValidInput() {
        Product product = new Product(1, 10D, "USD", 1, 1L);

        when(matchingRepositoryMock.findAll()).thenReturn(Stream.of(new Matching(1L, 1L)));
        when(productRepositoryMock.findAll()).thenReturn(Stream.of(product));
        when(productRepositoryMock.findProductsByMatchingId(1L)).thenReturn(Stream.of(product));
        initCurrencyMockWith(ImmutableMap.of("USD", 1D));

        List<AggregationResult> actual = streamToList(aggregationService.aggregateProducts());

        assertThat(actual, contains(new AggregationResult(1, 10D, 10D, "USD", 0)));
    }

    @Test
    public void shouldProcessAverageForSameCurrency() {
        Supplier<Stream<Product>> allProducts = supplierOfElements(
                new Product(1, 1000D, "USD", 1, 1L),
                new Product(1, 200D, "USD", 1, 1L)
        );
        when(matchingRepositoryMock.findAll()).thenReturn(Stream.of(new Matching(1L, 9999L)));
        when(productRepositoryMock.findAll()).then((s) -> allProducts.get());
        when(productRepositoryMock.findProductsByMatchingId(1L)).thenAnswer((s) -> allProducts.get());
        initCurrencyMockWith(ImmutableMap.of("USD", 1D));

        List<AggregationResult> actual = streamToList(aggregationService.aggregateProducts());

        assertThat(actual, hasSize(2));
        assertThat(actual.get(0).getAvgPrice(), is(600D));
        assertThat(actual.get(1).getAvgPrice(), is(600D));
    }

    @Test
    public void shouldProcessAverageForDifferentCurrencies() {
        Supplier<Stream<Product>> allProducts = supplierOfElements(
                new Product(1, 500D, "PLN", 1, 1L),
                new Product(1, 250D, "USD", 1, 1L)
        );
        when(matchingRepositoryMock.findAll()).thenReturn(Stream.of(new Matching(1L, 9999L)));
        when(productRepositoryMock.findAll()).then((s) -> allProducts.get());
        when(productRepositoryMock.findProductsByMatchingId(1L)).thenAnswer((s) -> allProducts.get());
        initCurrencyMockWith(ImmutableMap.of("PLN", 1D, "USD", 4D));

        List<AggregationResult> actual = streamToList(aggregationService.aggregateProducts());

        assertThat(actual, containsInAnyOrder(
                new AggregationResult(1, 500D, 750D, "PLN", 0), // 750 = 500 + (250 * 4) / 2
                new AggregationResult(1, 250D, 187.5D, "USD", 0) // 187.5 = 250 + (500 / 4) / 2
        ));
    }

    @Test
    public void shouldProcessTotalAndAvgForSameProducts() {
        Product product = new Product(1, 500D, "PLN", 10, 1L);
        when(matchingRepositoryMock.findAll()).thenReturn(Stream.of(new Matching(1L, 9999L)));
        when(productRepositoryMock.findAll()).thenReturn(Stream.of(product));
        when(productRepositoryMock.findProductsByMatchingId(1L)).thenReturn(Stream.of(product));
        initCurrencyMockWith(ImmutableMap.of("PLN", 1D));

        List<AggregationResult> actual = streamToList(aggregationService.aggregateProducts());

        assertThat(actual, contains(new AggregationResult(1, 5_000D, 500D, "PLN", 0)));
    }

    @Test
    public void shouldProcessTotalAndAvgForDifferentProducts() {
        Supplier<Stream<Product>> allProducts = supplierOfElements(
                new Product(1, 500D, "PLN", 10, 1L),
                new Product(1, 250D, "USD", 10, 1L)
        );
        when(matchingRepositoryMock.findAll()).thenReturn(Stream.of(new Matching(1L, 9999L)));
        when(productRepositoryMock.findAll()).then((s) -> allProducts.get());
        when(productRepositoryMock.findProductsByMatchingId(1L)).then((s) -> allProducts.get());
        initCurrencyMockWith(ImmutableMap.of("PLN", 1D, "USD", 4D));

        List<AggregationResult> actual = streamToList(aggregationService.aggregateProducts());

        assertThat(actual, containsInAnyOrder(
                new AggregationResult(1, 5_000D, 750D, "PLN", 0),
                new AggregationResult(1, 2_500D, 187.5D, "USD", 0)
        ));
    }

    @Test
    public void shouldSetIgnoredCount() {
        Supplier<Stream<Product>> allProducts = supplierOfElements(
                new Product(1, 500D, "PLN", 10, 1L),
                new Product(1, 250D, "PLN", 10, 1L)
        );
        when(matchingRepositoryMock.findAll()).thenReturn(Stream.of(new Matching(1L, 1L)));
        when(productRepositoryMock.findAll()).then((s) -> allProducts.get());
        when(productRepositoryMock.findProductsByMatchingId(1L)).then((s) -> allProducts.get());
        initCurrencyMockWith(ImmutableMap.of("PLN", 1D));

        List<AggregationResult> actual = streamToList(aggregationService.aggregateProducts());

        assertThat(actual, contains(new AggregationResult(1, 5_000D, 500D, "PLN", 1)));
    }

    private void initCurrencyMockWith(Map<String, Double> currencies) {
        currencies.forEach((currency, ratio) ->
                when(currencyRepositoryMock.getRatioForCurrency(currency))
                        .thenReturn(Optional.of(ratio))
        );
    }

    private <T> Supplier<Stream<T>> supplierOfElements(T... elements) {
        return () -> Stream.of(elements);
    }
}