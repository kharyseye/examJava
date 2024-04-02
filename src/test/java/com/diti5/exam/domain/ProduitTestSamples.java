package com.diti5.exam.domain;

import java.util.UUID;

public class ProduitTestSamples {

    public static Produit getProduitSample1() {
        return new Produit().id("id1").description("description1").nom("nom1");
    }

    public static Produit getProduitSample2() {
        return new Produit().id("id2").description("description2").nom("nom2");
    }

    public static Produit getProduitRandomSampleGenerator() {
        return new Produit().id(UUID.randomUUID().toString()).description(UUID.randomUUID().toString()).nom(UUID.randomUUID().toString());
    }
}
