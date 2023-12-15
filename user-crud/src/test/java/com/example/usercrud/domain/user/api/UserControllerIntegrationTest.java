package com.example.usercrud.domain.user.api;

import com.example.usercrud.UserCrudApplication;
import com.example.usercrud.domain.user.User;
import com.example.usercrud.domain.user.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static com.example.usercrud.PayloadBuilder.createPayload;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(
        classes = {UserCrudApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.flyway.clean-disabled=false"
)
@SpringJUnitConfig(UserCrudApplication.class)
class UserControllerIntegrationTest {

    private static final String URI = "/v1/user";
    private static final String URI_WITH_ID = "/v1/user/%s";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @AfterAll
    static void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }

    @BeforeEach()
    public void setup()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void onMissingRequestFieldsShouldReturnBadRequest() throws Exception {
        MockHttpServletRequestBuilder mockMvcBuilder = post(URI)
                .contentType(APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(createPayload(new CreateUserRequest(null, null, null, null)))
                .accept(APPLICATION_JSON);

        mockMvc.perform(mockMvcBuilder)
                .andDo(print())

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations", hasSize(4)))
                .andExpect(jsonPath("$.violations[0].field").value("birthday"))
                .andExpect(jsonPath("$.violations[0].message").value("must not be null"))
                .andExpect(jsonPath("$.violations[1].field").value("email"))
                .andExpect(jsonPath("$.violations[1].message").value("must not be null"))
                .andExpect(jsonPath("$.violations[2].field").value("firstName"))
                .andExpect(jsonPath("$.violations[2].message").value("must not be null"))
                .andExpect(jsonPath("$.violations[3].field").value("lastName"))
                .andExpect(jsonPath("$.violations[3].message").value("must not be null"));
    }

    @Test
    void onInvalidBirthdayAndEmailShouldReturnBadRequest() throws Exception {
        MockHttpServletRequestBuilder mockMvcBuilder = post(URI)
                .contentType(APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(createPayload(new CreateUserRequest(
                        "Harrier",
                        "Du Bois",
                        "email",
                        LocalDate.now().plusDays(1))))
                .accept(APPLICATION_JSON);

        mockMvc.perform(mockMvcBuilder)
                .andDo(print())

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations", hasSize(2)))
                .andExpect(jsonPath("$.violations[0].field").value("birthday"))
                .andExpect(jsonPath("$.violations[0].message").value("must be a date in the past or in the present"))
                .andExpect(jsonPath("$.violations[1].field").value("email"))
                .andExpect(jsonPath("$.violations[1].message").value("must be a well-formed email address"));
    }

    @Test
    void onValidCreateRequestShouldCreateUser() throws Exception {
        var createUserRequest = new CreateUserRequest(
                "Harrier",
                "Du Bois",
                "harrier.dubois@rcm.org",
                LocalDate.now().minusYears(30));
        MockHttpServletRequestBuilder mockMvcBuilder = post(URI)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(createPayload(createUserRequest));

        var result = mockMvc.perform(mockMvcBuilder)
                .andDo(print())

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(createUserRequest.firstName()))
                .andExpect(jsonPath("$.lastName").value(createUserRequest.lastName()))
                .andExpect(jsonPath("$.email").value(createUserRequest.email()))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.version").value(0))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        String createdUserId = JsonPath.read(responseContent, "$.id");
        String selfLink = JsonPath.read(responseContent,"$._links.self.href");
        assertTrue(userRepository.isUserPresent(UUID.fromString(createdUserId)));
        assertEquals("http://localhost/v1/user/" + createdUserId, selfLink);
    }

    @Test
    void onDeletedUserUpdateRequestShouldReturnBadRequest() throws Exception {
        User originalUser = createUser(false);
        var updateUserRequest = createUpdateUserRequest(originalUser.getVersion());
        MockHttpServletRequestBuilder mockMvcBuilder = put(String.format(URI_WITH_ID, originalUser.getId().toString()))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(createPayload(updateUserRequest));

        mockMvc.perform(mockMvcBuilder)
                .andDo(print())

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(
                        String.format("Entity User where id = %s not found", originalUser.getId())));
    }

    @Test
    void onInvalidUpdateRequestShouldReturnBadRequest() throws Exception {
        var updateUserRequest = new UpdateUserRequest(
                null,
                null,
                "tequila.sunset",
                LocalDate.now().plusYears(40),
                null);
        MockHttpServletRequestBuilder mockMvcBuilder = put(String.format(URI_WITH_ID, UUID.randomUUID()))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(createPayload(updateUserRequest));

        mockMvc.perform(mockMvcBuilder)
                .andDo(print())

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations", hasSize(5)))
                .andExpect(jsonPath("$.violations[0].field").value("birthday"))
                .andExpect(jsonPath("$.violations[0].message").value("must be a date in the past or in the present"))
                .andExpect(jsonPath("$.violations[1].field").value("email"))
                .andExpect(jsonPath("$.violations[1].message").value("must be a well-formed email address"))
                .andExpect(jsonPath("$.violations[2].field").value("firstName"))
                .andExpect(jsonPath("$.violations[2].message").value("must not be null"))
                .andExpect(jsonPath("$.violations[3].field").value("lastName"))
                .andExpect(jsonPath("$.violations[3].message").value("must not be null"))
                .andExpect(jsonPath("$.violations[4].field").value("version"))
                .andExpect(jsonPath("$.violations[4].message").value("must not be null"));
    }

    @Test
    void onValidUpdateRequestShouldUpdateProfile() throws Exception {
        User originalUser = createUser(true);
        var updateUserRequest = createUpdateUserRequest(originalUser.getVersion());
        MockHttpServletRequestBuilder mockMvcBuilder = put(String.format(URI_WITH_ID, originalUser.getId().toString()))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(createPayload(updateUserRequest));

        mockMvc.perform(mockMvcBuilder)
                .andDo(print())

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(updateUserRequest.firstName()))
                .andExpect(jsonPath("$.lastName").value(updateUserRequest.lastName()))
                .andExpect(jsonPath("$.email").value(updateUserRequest.email()))
                .andExpect(jsonPath("$.age").value(40))
                .andExpect(jsonPath("$.version").value(1))
                .andReturn();
        User updatedUser = userRepository.findByIdAndActiveTrue(originalUser.getId()).orElseThrow();
        assertThat(updatedUser)
                .extracting(User::getFirstName, User::getLastName, User::getEmail, User::getBirthday)
                .containsExactly(
                        updateUserRequest.firstName(),
                        updateUserRequest.lastName(),
                        updateUserRequest.email(),
                        updateUserRequest.birthday());
    }

    @Test
    void onUpdateRequestWithOutdatedVersionShouldReturnConflictAndNotUpdateProfile() throws Exception {
        User originalUser = createUser(true);
        MockHttpServletRequestBuilder mockMvcBuilder = put(String.format(URI_WITH_ID, originalUser.getId().toString()))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(createPayload(createUpdateUserRequest(originalUser.getVersion() - 1)));

        mockMvc.perform(mockMvcBuilder)
                .andDo(print())

                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("The resource is updated on the server side. Get its latest version and retry your request."))
                .andReturn();
        User updatedUser = userRepository.findByIdAndActiveTrue(originalUser.getId()).orElseThrow();
        assertThat(updatedUser)
                .extracting(User::getFirstName, User::getLastName, User::getEmail, User::getBirthday)
                .containsExactly(
                        originalUser.getFirstName(),
                        originalUser.getLastName(),
                        originalUser.getEmail(),
                        originalUser.getBirthday());
    }

    @Test
    void onValidGetRequestShouldReturnUser() throws Exception {
        User originalUser = createUser(true);
        MockHttpServletRequestBuilder mockMvcBuilder = get(String.format(URI_WITH_ID, originalUser.getId().toString()))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

        mockMvc.perform(mockMvcBuilder)
                .andDo(print())

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(originalUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(originalUser.getLastName()))
                .andExpect(jsonPath("$.email").value(originalUser.getEmail()))
                .andExpect(jsonPath("$.age").value(originalUser.calculateAge()));
    }

    @Test
    void onDeletedUserGetRequestShouldReturnNotFound() throws Exception {
        var originalUser = createUser(false);
        MockHttpServletRequestBuilder mockMvcBuilder = get(String.format(URI_WITH_ID, originalUser.getId().toString()))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8);

        mockMvc.perform(mockMvcBuilder)
                .andDo(print())

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(
                        String.format("Entity User where id = %s not found", originalUser.getId())));
    }

    @Test
    void onValidDeleteRequestShouldSoftDeleteUser() throws Exception {
        User originalUser = createUser(true);
        MockHttpServletRequestBuilder mockMvcBuilder = delete(String.format(URI_WITH_ID, originalUser.getId().toString()))
                .characterEncoding(StandardCharsets.UTF_8);

        mockMvc.perform(mockMvcBuilder)
                .andDo(print())

                .andExpect(status().isOk());
        assertThat(userRepository.findByIdAndActiveTrue(originalUser.getId())).isEmpty();
        assertThat(userRepository.findById(originalUser.getId())).isNotEmpty();
    }

    @Test
    void onDeletedUserDeleteRequestShouldReturnNotFound() throws Exception {
        User originalUser = createUser(false);
        MockHttpServletRequestBuilder mockMvcBuilder = delete(String.format(URI_WITH_ID, originalUser.getId().toString()))
                .characterEncoding(StandardCharsets.UTF_8);

        mockMvc.perform(mockMvcBuilder)
                .andDo(print())

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(
                        String.format("Entity User where id = %s not found", originalUser.getId())));
    }

    private User createUser(boolean active) {
        var user = new User(
                "Harrier",
                "Du Bois",
                "harrier.dubois@rcm.org",
                LocalDate.now().minusYears(30));
        user.setActive(active);

        return userRepository.save(user);
    }

    private UpdateUserRequest createUpdateUserRequest(Long version) {
        return new UpdateUserRequest(
                "Tequila",
                "Sunset",
                "tequila.sunset@rcm.org",
                LocalDate.now().minusYears(40),
                version);
    }
}
