package com.diti5.exam.repository;

import com.diti5.exam.domain.Produit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data MongoDB reactive repository for the Produit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProduitRepository extends ReactiveMongoRepository<Produit, String> {
    Flux<Produit> findAllBy(Pageable pageable);
}
