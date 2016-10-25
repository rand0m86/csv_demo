package com.example.repositories;

import com.example.pojo.Product;

import java.util.stream.Stream;

/**
 * Product repository. Contains operations available for products retrieval.
 */
public interface ProductRepository {

    /**
     * Returns all products as stream.
     */
    Stream<Product> findAll();

    /**
     * Returns products with given matchingId as stream.
     *
     * @param matchingId The matching id
     */
    Stream<Product> findProductsByMatchingId(Long matchingId);
}
