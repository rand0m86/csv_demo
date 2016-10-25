package com.example.repositories.csv;

import com.example.repositories.MatchingRepository;
import com.example.pojo.Matching;
import com.example.util.CsvRow;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Matching repository implementation for database given as CSV file.
 *
 * @see MatchingRepository
 */
public class MatchingRepositoryImpl extends AbstractRepositoryImpl<Matching> implements MatchingRepository {

    public MatchingRepositoryImpl(String csvFileName) {
        super(csvFileName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Matching> getMatchingById(Long id) {
        return getRepository()
                .filter(m -> m.getMatchingId() == id)
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Matching> findAll() {
        return getRepository();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Matching toEntity(CsvRow row) {
        return new Matching(
                row.get("matching_id", Long::valueOf).get(),
                row.get("top_priced_count", Long::valueOf).get());
    }
}
