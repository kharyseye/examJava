package com.diti5.exam.service;

import com.diti5.exam.domain.Produit;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.diti5.exam.domain.Produit}.
 */
public interface ProduitService {
    /**
     * Save a produit.
     *
     * @param produit the entity to save.
     * @return the persisted entity.
     */
    Mono<Produit> save(Produit produit);

    /**
     * Updates a produit.
     *
     * @param produit the entity to update.
     * @return the persisted entity.
     */
    Mono<Produit> update(Produit produit);

    /**
     * Partially updates a produit.
     *
     * @param produit the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Produit> partialUpdate(Produit produit);

    /**
     * Get all the produits.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Produit> findAll(Pageable pageable);

    /**
     * Returns the number of produits available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" produit.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Produit> findOne(String id);

    /**
     * Delete the "id" produit.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);
}
