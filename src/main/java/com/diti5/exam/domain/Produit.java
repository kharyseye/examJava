package com.diti5.exam.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Produit.
 */
@Document(collection = "produit")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Produit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("description")
    private String description;

    @Field("nom")
    private String nom;

    @Field("prix")
    private Double prix;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Produit id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Produit description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNom() {
        return this.nom;
    }

    public Produit nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Double getPrix() {
        return this.prix;
    }

    public Produit prix(Double prix) {
        this.setPrix(prix);
        return this;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Produit)) {
            return false;
        }
        return getId() != null && getId().equals(((Produit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Produit{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", nom='" + getNom() + "'" +
            ", prix=" + getPrix() +
            "}";
    }
}
