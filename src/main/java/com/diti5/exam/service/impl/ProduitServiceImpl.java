package com.diti5.exam.service.impl;

import com.diti5.exam.domain.Produit;
import com.diti5.exam.repository.ProduitRepository;
import com.diti5.exam.service.ProduitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.diti5.exam.domain.Produit}.
 */
@Service
public class ProduitServiceImpl implements ProduitService {

    private final Logger log = LoggerFactory.getLogger(ProduitServiceImpl.class);

    private final ProduitRepository produitRepository;

    public ProduitServiceImpl(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    @Override
    public Mono<Produit> save(Produit produit) {
        log.debug("Request to save Produit : {}", produit);
        return produitRepository.save(produit);
    }

    @Override
    public Mono<Produit> update(Produit produit) {
        log.debug("Request to update Produit : {}", produit);
        return produitRepository.save(produit);
    }

    @Override
    public Mono<Produit> partialUpdate(Produit produit) {
        log.debug("Request to partially update Produit : {}", produit);

        return produitRepository
            .findById(produit.getId())
            .map(existingProduit -> {
                if (produit.getDescription() != null) {
                    existingProduit.setDescription(produit.getDescription());
                }
                if (produit.getNom() != null) {
                    existingProduit.setNom(produit.getNom());
                }
                if (produit.getPrix() != null) {
                    existingProduit.setPrix(produit.getPrix());
                }

                return existingProduit;
            })
            .flatMap(produitRepository::save);
    }

    @Override
    public Flux<Produit> findAll(Pageable pageable) {
        log.debug("Request to get all Produits");
        return produitRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return produitRepository.count();
    }

    @Override
    public Mono<Produit> findOne(String id) {
        log.debug("Request to get Produit : {}", id);
        return produitRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Produit : {}", id);
        return produitRepository.deleteById(id);
    }
}
