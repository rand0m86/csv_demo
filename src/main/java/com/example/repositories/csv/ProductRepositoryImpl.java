package com.example.repositories.csv;

import com.example.pojo.Product;
import com.example.repositories.ProductRepository;
import com.example.util.CsvRow;

import java.util.stream.Stream;

/**
 * Product repository implementation for database given as CSV file.
 *
 * @see ProductRepository
 */
public class ProductRepositoryImpl extends AbstractRepositoryImpl<Product> implements ProductRepository {

    public ProductRepositoryImpl(String csvFileName) {
        super(csvFileName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Product> findAll() {
        return getRepository();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Product> findProductsByMatchingId(Long matchingId) {
        return getRepository()
                .filter(p -> p.getMatchingId() == matchingId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Product toEntity(CsvRow row) {
        return new Product(
                row.get("id", Long::valueOf).get(),
                row.get("price", Double::valueOf).get(),
                row.get("currency").get(),
                row.get("quantity", Integer::valueOf).get(),
                row.get("matching_id", Long::valueOf).get()
        );
    }
}
