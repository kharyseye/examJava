package com.diti5.exam.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.diti5.exam.IntegrationTest;
import com.diti5.exam.domain.Produit;
import com.diti5.exam.repository.ProduitRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ProduitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProduitResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final Double DEFAULT_PRIX = 1D;
    private static final Double UPDATED_PRIX = 2D;

    private static final String ENTITY_API_URL = "/api/produits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Produit produit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produit createEntity() {
        Produit produit = new Produit().description(DEFAULT_DESCRIPTION).nom(DEFAULT_NOM).prix(DEFAULT_PRIX);
        return produit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produit createUpdatedEntity() {
        Produit produit = new Produit().description(UPDATED_DESCRIPTION).nom(UPDATED_NOM).prix(UPDATED_PRIX);
        return produit;
    }

    @BeforeEach
    public void initTest() {
        produitRepository.deleteAll().block();
        produit = createEntity();
    }

    @Test
    void createProduit() throws Exception {
        int databaseSizeBeforeCreate = produitRepository.findAll().collectList().block().size();
        // Create the Produit
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeCreate + 1);
        Produit testProduit = produitList.get(produitList.size() - 1);
        assertThat(testProduit.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProduit.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testProduit.getPrix()).isEqualTo(DEFAULT_PRIX);
    }

    @Test
    void createProduitWithExistingId() throws Exception {
        // Create the Produit with an existing ID
        produit.setId("existing_id");

        int databaseSizeBeforeCreate = produitRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllProduits() {
        // Initialize the database
        produitRepository.save(produit).block();

        // Get all the produitList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(produit.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM))
            .jsonPath("$.[*].prix")
            .value(hasItem(DEFAULT_PRIX.doubleValue()));
    }

    @Test
    void getProduit() {
        // Initialize the database
        produitRepository.save(produit).block();

        // Get the produit
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, produit.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(produit.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.nom")
            .value(is(DEFAULT_NOM))
            .jsonPath("$.prix")
            .value(is(DEFAULT_PRIX.doubleValue()));
    }

    @Test
    void getNonExistingProduit() {
        // Get the produit
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProduit() throws Exception {
        // Initialize the database
        produitRepository.save(produit).block();

        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();

        // Update the produit
        Produit updatedProduit = produitRepository.findById(produit.getId()).block();
        updatedProduit.description(UPDATED_DESCRIPTION).nom(UPDATED_NOM).prix(UPDATED_PRIX);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedProduit.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedProduit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
        Produit testProduit = produitList.get(produitList.size() - 1);
        assertThat(testProduit.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduit.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testProduit.getPrix()).isEqualTo(UPDATED_PRIX);
    }

    @Test
    void putNonExistingProduit() throws Exception {
        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();
        produit.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, produit.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProduit() throws Exception {
        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();
        produit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProduit() throws Exception {
        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();
        produit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProduitWithPatch() throws Exception {
        // Initialize the database
        produitRepository.save(produit).block();

        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();

        // Update the produit using partial update
        Produit partialUpdatedProduit = new Produit();
        partialUpdatedProduit.setId(produit.getId());

        partialUpdatedProduit.nom(UPDATED_NOM).prix(UPDATED_PRIX);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProduit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProduit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
        Produit testProduit = produitList.get(produitList.size() - 1);
        assertThat(testProduit.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProduit.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testProduit.getPrix()).isEqualTo(UPDATED_PRIX);
    }

    @Test
    void fullUpdateProduitWithPatch() throws Exception {
        // Initialize the database
        produitRepository.save(produit).block();

        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();

        // Update the produit using partial update
        Produit partialUpdatedProduit = new Produit();
        partialUpdatedProduit.setId(produit.getId());

        partialUpdatedProduit.description(UPDATED_DESCRIPTION).nom(UPDATED_NOM).prix(UPDATED_PRIX);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProduit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProduit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
        Produit testProduit = produitList.get(produitList.size() - 1);
        assertThat(testProduit.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProduit.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testProduit.getPrix()).isEqualTo(UPDATED_PRIX);
    }

    @Test
    void patchNonExistingProduit() throws Exception {
        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();
        produit.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, produit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProduit() throws Exception {
        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();
        produit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProduit() throws Exception {
        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();
        produit.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProduit() {
        // Initialize the database
        produitRepository.save(produit).block();

        int databaseSizeBeforeDelete = produitRepository.findAll().collectList().block().size();

        // Delete the produit
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, produit.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
