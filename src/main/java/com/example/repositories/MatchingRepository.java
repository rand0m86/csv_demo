package com.example.repositories;

import com.example.pojo.Matching;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Matching repository. Contains operations available for matchings retrieval.
 */
public interface MatchingRepository {

    /**
     * Returns all matchings as stream.
     */
    Stream<Matching> findAll();

    /**
     * Returns matching for the given id or empty {@code Optional} if no matching
     * exist.
     *
     * @param id The matching id
     */
    Optional<Matching> getMatchingById(Long id);
}
