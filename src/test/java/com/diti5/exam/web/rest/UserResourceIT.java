package com.diti5.exam.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.diti5.exam.IntegrationTest;
import com.diti5.exam.domain.Authority;
import com.diti5.exam.domain.User;
import com.diti5.exam.repository.UserRepository;
import com.diti5.exam.security.AuthoritiesConstants;
import com.diti5.exam.service.dto.AdminUserDTO;
import com.diti5.exam.service.mapper.UserMapper;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link UserResource} REST controller.
 */
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_TIMEOUT)
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@IntegrationTest
class UserResourceIT {

    private static final String DEFAULT_LOGIN = "johndoe";
    private static final String UPDATED_LOGIN = "jhipster";

    private static final String DEFAULT_ID = "id1";

    private static final String DEFAULT_PASSWORD = "passjohndoe";
    private static final String UPDATED_PASSWORD = "passjhipster";

    private static final String DEFAULT_EMAIL = "johndoe@localhost";
    private static final String UPDATED_EMAIL = "jhipster@localhost";

    private static final String DEFAULT_FIRSTNAME = "john";
    private static final String UPDATED_FIRSTNAME = "jhipsterFirstName";

    private static final String DEFAULT_LASTNAME = "doe";
    private static final String UPDATED_LASTNAME = "jhipsterLastName";

    private static final String DEFAULT_IMAGEURL = "http://placehold.it/50x50";
    private static final String UPDATED_IMAGEURL = "http://placehold.it/40x40";

    private static final String DEFAULT_LANGKEY = "en";
    private static final String UPDATED_LANGKEY = "fr";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WebTestClient webTestClient;

    private User user;

    /**
     * Create a User.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which has a required relationship to the User entity.
     */
    public static User createEntity() {
        User user = new User();
        user.setLogin(DEFAULT_LOGIN);
        user.setPassword(RandomStringUtils.randomAlphanumeric(60));
        user.setActivated(true);
        user.setEmail(DEFAULT_EMAIL);
        user.setFirstName(DEFAULT_FIRSTNAME);
        user.setLastName(DEFAULT_LASTNAME);
        user.setImageUrl(DEFAULT_IMAGEURL);
        user.setLangKey(DEFAULT_LANGKEY);
        return user;
    }

    /**
     * Setups the database with one user.
     */
    public static User initTestUser(UserRepository userRepository) {
        userRepository.deleteAll().block();
        User user = createEntity();
        return user;
    }

    @BeforeEach
    public void initTest() {
        user = initTestUser(userRepository);
    }

    @Test
    void createUser() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().collectList().block().size();

        // Create the User
        AdminUserDTO user = new AdminUserDTO();
        user.setLogin(DEFAULT_LOGIN);
        user.setFirstName(DEFAULT_FIRSTNAME);
        user.setLastName(DEFAULT_LASTNAME);
        user.setEmail(DEFAULT_EMAIL);
        user.setActivated(true);
        user.setImageUrl(DEFAULT_IMAGEURL);
        user.setLangKey(DEFAULT_LANGKEY);
        user.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        webTestClient
            .post()
            .uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(user))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the User in the database
        assertPersistedUsers(users -> {
            assertThat(users).hasSize(databaseSizeBeforeCreate + 1);
            User testUser = users.get(users.size() - 1);
            assertThat(testUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
            assertThat(testUser.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
            assertThat(testUser.getLastName()).isEqualTo(DEFAULT_LASTNAME);
            assertThat(testUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
            assertThat(testUser.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
            assertThat(testUser.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
        });
        userRepository.deleteAll();
    }

    @Test
    void createUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().collectList().block().size();

        AdminUserDTO user = new AdminUserDTO();
        user.setId("1L");
        user.setLogin(DEFAULT_LOGIN);
        user.setFirstName(DEFAULT_FIRSTNAME);
        user.setLastName(DEFAULT_LASTNAME);
        user.setEmail(DEFAULT_EMAIL);
        user.setActivated(true);
        user.setImageUrl(DEFAULT_IMAGEURL);
        user.setLangKey(DEFAULT_LANGKEY);
        user.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(user))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the User in the database
        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
        userRepository.deleteAll();
    }

    @Test
    void createUserWithExistingLogin() throws Exception {
        // Initialize the database
        userRepository.save(user).block();
        int databaseSizeBeforeCreate = userRepository.findAll().collectList().block().size();

        AdminUserDTO user = new AdminUserDTO();
        user.setLogin(DEFAULT_LOGIN); // this login should already be used
        user.setFirstName(DEFAULT_FIRSTNAME);
        user.setLastName(DEFAULT_LASTNAME);
        user.setEmail("anothermail@localhost");
        user.setActivated(true);
        user.setImageUrl(DEFAULT_IMAGEURL);
        user.setLangKey(DEFAULT_LANGKEY);
        user.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        // Create the User
        webTestClient
            .post()
            .uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(user))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the User in the database
        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
        userRepository.deleteAll();
    }

    @Test
    void createUserWithExistingEmail() throws Exception {
        // Initialize the database
        userRepository.save(user).block();
        int databaseSizeBeforeCreate = userRepository.findAll().collectList().block().size();

        AdminUserDTO user = new AdminUserDTO();
        user.setLogin("anotherlogin");
        user.setFirstName(DEFAULT_FIRSTNAME);
        user.setLastName(DEFAULT_LASTNAME);
        user.setEmail(DEFAULT_EMAIL); // this email should already be used
        user.setActivated(true);
        user.setImageUrl(DEFAULT_IMAGEURL);
        user.setLangKey(DEFAULT_LANGKEY);
        user.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        // Create the User
        webTestClient
            .post()
            .uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(user))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the User in the database
        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers() {
        // Initialize the database
        userRepository.save(user).block();

        // Get all the users
        AdminUserDTO foundUser = webTestClient
            .get()
            .uri("/api/admin/users?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .returnResult(AdminUserDTO.class)
            .getResponseBody()
            .blockFirst();

        assertThat(foundUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(foundUser.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(foundUser.getLastName()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(foundUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(foundUser.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
        assertThat(foundUser.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
        userRepository.deleteAll();
    }

    @Test
    void getUser() {
        // Initialize the database
        userRepository.save(user).block();

        // Get the user
        webTestClient
            .get()
            .uri("/api/admin/users/{login}", user.getLogin())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.login")
            .isEqualTo(user.getLogin())
            .jsonPath("$.firstName")
            .isEqualTo(DEFAULT_FIRSTNAME)
            .jsonPath("$.lastName")
            .isEqualTo(DEFAULT_LASTNAME)
            .jsonPath("$.email")
            .isEqualTo(DEFAULT_EMAIL)
            .jsonPath("$.imageUrl")
            .isEqualTo(DEFAULT_IMAGEURL)
            .jsonPath("$.langKey")
            .isEqualTo(DEFAULT_LANGKEY);

        userRepository.deleteAll();
    }

    @Test
    void getNonExistingUser() {
        webTestClient.get().uri("/api/admin/users/unknown").exchange().expectStatus().isNotFound();
    }

    @Test
    void updateUser() throws Exception {
        // Initialize the database
        userRepository.save(user).block();
        int databaseSizeBeforeUpdate = userRepository.findAll().collectList().block().size();

        // Update the user
        User updatedUser = userRepository.findById(user.getId()).block();

        AdminUserDTO user = new AdminUserDTO();
        user.setId(updatedUser.getId());
        user.setLogin(updatedUser.getLogin());
        user.setFirstName(UPDATED_FIRSTNAME);
        user.setLastName(UPDATED_LASTNAME);
        user.setEmail(UPDATED_EMAIL);
        user.setActivated(updatedUser.isActivated());
        user.setImageUrl(UPDATED_IMAGEURL);
        user.setLangKey(UPDATED_LANGKEY);
        user.setCreatedBy(updatedUser.getCreatedBy());
        user.setCreatedDate(updatedUser.getCreatedDate());
        user.setLastModifiedBy(updatedUser.getLastModifiedBy());
        user.setLastModifiedDate(updatedUser.getLastModifiedDate());
        user.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        webTestClient
            .put()
            .uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(user))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the User in the database
        assertPersistedUsers(users -> {
            assertThat(users).hasSize(databaseSizeBeforeUpdate);
            User testUser = users.stream().filter(usr -> usr.getId().equals(updatedUser.getId())).findFirst().orElseThrow();
            assertThat(testUser.getFirstName()).isEqualTo(UPDATED_FIRSTNAME);
            assertThat(testUser.getLastName()).isEqualTo(UPDATED_LASTNAME);
            assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
            assertThat(testUser.getImageUrl()).isEqualTo(UPDATED_IMAGEURL);
            assertThat(testUser.getLangKey()).isEqualTo(UPDATED_LANGKEY);
        });
        userRepository.deleteAll();
    }

    @Test
    void updateUserLogin() throws Exception {
        // Initialize the database
        userRepository.save(user).block();
        int databaseSizeBeforeUpdate = userRepository.findAll().collectList().block().size();

        // Update the user
        User updatedUser = userRepository.findById(user.getId()).block();

        AdminUserDTO user = new AdminUserDTO();
        user.setId(updatedUser.getId());
        user.setLogin(UPDATED_LOGIN);
        user.setFirstName(UPDATED_FIRSTNAME);
        user.setLastName(UPDATED_LASTNAME);
        user.setEmail(UPDATED_EMAIL);
        user.setActivated(updatedUser.isActivated());
        user.setImageUrl(UPDATED_IMAGEURL);
        user.setLangKey(UPDATED_LANGKEY);
        user.setCreatedBy(updatedUser.getCreatedBy());
        user.setCreatedDate(updatedUser.getCreatedDate());
        user.setLastModifiedBy(updatedUser.getLastModifiedBy());
        user.setLastModifiedDate(updatedUser.getLastModifiedDate());
        user.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        webTestClient
            .put()
            .uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(user))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the User in the database
        assertPersistedUsers(users -> {
            assertThat(users).hasSize(databaseSizeBeforeUpdate);
            User testUser = users.stream().filter(usr -> usr.getId().equals(updatedUser.getId())).findFirst().orElseThrow();
            assertThat(testUser.getLogin()).isEqualTo(UPDATED_LOGIN);
            assertThat(testUser.getFirstName()).isEqualTo(UPDATED_FIRSTNAME);
            assertThat(testUser.getLastName()).isEqualTo(UPDATED_LASTNAME);
            assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
            assertThat(testUser.getImageUrl()).isEqualTo(UPDATED_IMAGEURL);
            assertThat(testUser.getLangKey()).isEqualTo(UPDATED_LANGKEY);
        });
        userRepository.deleteAll();
    }

    @Test
    void updateUserExistingEmail() throws Exception {
        // Initialize the database with 2 users
        userRepository.save(user).block();

        User anotherUser = new User();
        anotherUser.setLogin("jhipster");
        anotherUser.setPassword(RandomStringUtils.randomAlphanumeric(60));
        anotherUser.setActivated(true);
        anotherUser.setEmail("jhipster@localhost");
        anotherUser.setFirstName("java");
        anotherUser.setLastName("hipster");
        anotherUser.setImageUrl("");
        anotherUser.setLangKey("en");
        userRepository.save(anotherUser).block();

        // Update the user
        User updatedUser = userRepository.findById(user.getId()).block();

        AdminUserDTO user = new AdminUserDTO();
        user.setId(updatedUser.getId());
        user.setLogin(updatedUser.getLogin());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail("jhipster@localhost"); // this email should already be used by anotherUser
        user.setActivated(updatedUser.isActivated());
        user.setImageUrl(updatedUser.getImageUrl());
        user.setLangKey(updatedUser.getLangKey());
        user.setCreatedBy(updatedUser.getCreatedBy());
        user.setCreatedDate(updatedUser.getCreatedDate());
        user.setLastModifiedBy(updatedUser.getLastModifiedBy());
        user.setLastModifiedDate(updatedUser.getLastModifiedDate());
        user.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        webTestClient
            .put()
            .uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(user))
            .exchange()
            .expectStatus()
            .isBadRequest();
        userRepository.deleteAll();
    }

    @Test
    void updateUserExistingLogin() throws Exception {
        // Initialize the database
        userRepository.save(user).block();

        User anotherUser = new User();
        anotherUser.setLogin("jhipster");
        anotherUser.setPassword(RandomStringUtils.randomAlphanumeric(60));
        anotherUser.setActivated(true);
        anotherUser.setEmail("jhipster@localhost");
        anotherUser.setFirstName("java");
        anotherUser.setLastName("hipster");
        anotherUser.setImageUrl("");
        anotherUser.setLangKey("en");
        userRepository.save(anotherUser).block();

        // Update the user
        User updatedUser = userRepository.findById(user.getId()).block();

        AdminUserDTO user = new AdminUserDTO();
        user.setId(updatedUser.getId());
        user.setLogin("jhipster"); // this login should already be used by anotherUser
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        user.setActivated(updatedUser.isActivated());
        user.setImageUrl(updatedUser.getImageUrl());
        user.setLangKey(updatedUser.getLangKey());
        user.setCreatedBy(updatedUser.getCreatedBy());
        user.setCreatedDate(updatedUser.getCreatedDate());
        user.setLastModifiedBy(updatedUser.getLastModifiedBy());
        user.setLastModifiedDate(updatedUser.getLastModifiedDate());
        user.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        webTestClient
            .put()
            .uri("/api/admin/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(user))
            .exchange()
            .expectStatus()
            .isBadRequest();
        userRepository.deleteAll();
    }

    @Test
    void deleteUser() {
        // Initialize the database
        userRepository.save(user).block();
        int databaseSizeBeforeDelete = userRepository.findAll().collectList().block().size();

        // Delete the user
        webTestClient
            .delete()
            .uri("/api/admin/users/{login}", user.getLogin())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database is empty
        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeDelete - 1));
    }

    @Test
    void testUserEquals() throws Exception {
        TestUtil.equalsVerifier(User.class);
        User user1 = new User();
        user1.setId(DEFAULT_ID);
        User user2 = new User();
        user2.setId(user1.getId());
        assertThat(user1).isEqualTo(user2);
        user2.setId("id2");
        assertThat(user1).isNotEqualTo(user2);
        user1.setId(null);
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void testUserDTOtoUser() {
        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setId(DEFAULT_ID);
        userDTO.setLogin(DEFAULT_LOGIN);
        userDTO.setFirstName(DEFAULT_FIRSTNAME);
        userDTO.setLastName(DEFAULT_LASTNAME);
        userDTO.setEmail(DEFAULT_EMAIL);
        userDTO.setActivated(true);
        userDTO.setImageUrl(DEFAULT_IMAGEURL);
        userDTO.setLangKey(DEFAULT_LANGKEY);
        userDTO.setCreatedBy(DEFAULT_LOGIN);
        userDTO.setLastModifiedBy(DEFAULT_LOGIN);
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        User user = userMapper.userDTOToUser(userDTO);
        assertThat(user.getId()).isEqualTo(DEFAULT_ID);
        assertThat(user.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(user.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(user.getLastName()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(user.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(user.isActivated()).isTrue();
        assertThat(user.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
        assertThat(user.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
        assertThat(user.getCreatedBy()).isNull();
        assertThat(user.getCreatedDate()).isNotNull();
        assertThat(user.getLastModifiedBy()).isNull();
        assertThat(user.getLastModifiedDate()).isNotNull();
        assertThat(user.getAuthorities()).extracting("name").containsExactly(AuthoritiesConstants.USER);
    }

    @Test
    void testUserToUserDTO() {
        user.setId(DEFAULT_ID);
        user.setCreatedBy(DEFAULT_LOGIN);
        user.setCreatedDate(Instant.now());
        user.setLastModifiedBy(DEFAULT_LOGIN);
        user.setLastModifiedDate(Instant.now());
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        authorities.add(authority);
        user.setAuthorities(authorities);

        AdminUserDTO userDTO = userMapper.userToAdminUserDTO(user);

        assertThat(userDTO.getId()).isEqualTo(DEFAULT_ID);
        assertThat(userDTO.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(userDTO.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(userDTO.getLastName()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(userDTO.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(userDTO.isActivated()).isTrue();
        assertThat(userDTO.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
        assertThat(userDTO.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
        assertThat(userDTO.getCreatedBy()).isEqualTo(DEFAULT_LOGIN);
        assertThat(userDTO.getCreatedDate()).isEqualTo(user.getCreatedDate());
        assertThat(userDTO.getLastModifiedBy()).isEqualTo(DEFAULT_LOGIN);
        assertThat(userDTO.getLastModifiedDate()).isEqualTo(user.getLastModifiedDate());
        assertThat(userDTO.getAuthorities()).containsExactly(AuthoritiesConstants.USER);
        assertThat(userDTO.toString()).isNotNull();
    }

    @Test
    void testAuthorityEquals() {
        Authority authorityA = new Authority();
        assertThat(authorityA).isNotEqualTo(null).isNotEqualTo(new Object());
        assertThat(authorityA.hashCode()).isZero();
        assertThat(authorityA.toString()).isNotNull();

        Authority authorityB = new Authority();
        assertThat(authorityA).isEqualTo(authorityB);

        authorityB.setName(AuthoritiesConstants.ADMIN);
        assertThat(authorityA).isNotEqualTo(authorityB);

        authorityA.setName(AuthoritiesConstants.USER);
        assertThat(authorityA).isNotEqualTo(authorityB);

        authorityB.setName(AuthoritiesConstants.USER);
        assertThat(authorityA).isEqualTo(authorityB).hasSameHashCodeAs(authorityB);
    }

    private void assertPersistedUsers(Consumer<List<User>> userAssertion) {
        userAssertion.accept(userRepository.findAll().collectList().block());
    }
}
