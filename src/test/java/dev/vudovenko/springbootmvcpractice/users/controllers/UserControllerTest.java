package dev.vudovenko.springbootmvcpractice.users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vudovenko.springbootmvcpractice.exceptionHandling.dto.ErrorMessageResponse;
import dev.vudovenko.springbootmvcpractice.pets.model.Pet;
import dev.vudovenko.springbootmvcpractice.pets.services.PetService;
import dev.vudovenko.springbootmvcpractice.users.model.User;
import dev.vudovenko.springbootmvcpractice.users.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;

    @Test
    void shouldSuccessCreateUser() throws Exception {
        User userToCreate = new User(
                null,
                "user1",
                "user1@user1.com",
                20,
                List.of()
        );

        String userJson = objectMapper.writeValueAsString(userToCreate);

        String createdUserJson = mockMvc
                .perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User userResponse = objectMapper.readValue(createdUserJson, User.class);

        Assertions.assertNotNull(userResponse.getId());
        Assertions.assertNotNull(userResponse.getPets());
        Assertions.assertTrue(userResponse.getPets().isEmpty());
        org.assertj.core.api.Assertions
                .assertThat(userResponse)
                .usingRecursiveComparison()
                .ignoringFields("id", "pets")
                .isEqualTo(userToCreate);
    }

    @Test
    void shouldNotCreateUserWhenRequestNotValid() throws Exception {
        User userToCreate = new User(
                Long.MAX_VALUE,
                "       ",
                "wrong@format@email.com",
                121,
                List.of(new Pet(), new Pet(), new Pet())
        );

        String userJson = objectMapper.writeValueAsString(userToCreate);

        String errorMessageResponseJson = mockMvc
                .perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson)
                )
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorMessageResponse errorMessageResponse = objectMapper
                .readValue(errorMessageResponseJson, ErrorMessageResponse.class);

        String detailedMessage = errorMessageResponse.detailedMessage();

        Assertions.assertEquals(errorMessageResponse.message(), "Request validation failed");
        Assertions.assertTrue(detailedMessage.contains("id:"));
        Assertions.assertTrue(detailedMessage.contains("name:"));
        Assertions.assertTrue(detailedMessage.contains("email:"));
        Assertions.assertTrue(detailedMessage.contains("age:"));
        Assertions.assertTrue(detailedMessage.contains("pets:"));
        Assertions.assertNotNull(errorMessageResponse.dateTime());
    }

    @Test
    void shouldSuccessUpdateUser() throws Exception {
        User createdUser = userService.createUser(
                new User(
                        null,
                        "user1",
                        "user1@user1.com",
                        20,
                        List.of()
                )
        );

        User userToUpdate = new User(
                null,
                "user2",
                "user2@user2.com",
                55,
                null
        );

        String userToUpdateJson = objectMapper.writeValueAsString(userToUpdate);

        String updatedUserJson = mockMvc
                .perform(
                        put("/users/{id}", createdUser.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userToUpdateJson)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User updatedUser = objectMapper.readValue(updatedUserJson, User.class);

        Assertions.assertEquals(updatedUser.getId(), createdUser.getId());
        Assertions.assertNotNull(updatedUser.getPets());
        Assertions.assertTrue(updatedUser.getPets().isEmpty());
        org.assertj.core.api.Assertions
                .assertThat(updatedUser)
                .usingRecursiveComparison()
                .ignoringFields("id", "pets")
                .isEqualTo(userToUpdate);
    }

    @Test
    void shouldNotUpdateUserWhenRequestNotValid() throws Exception {
        User createdUser = userService.createUser(
                new User(
                        null,
                        "user22",
                        "user22@user2.com",
                        44,
                        null
                )
        );

        User userToUpdate = new User(
                null,
                "us",
                "user2#user2?com",
                -44,
                null
        );

        String userToUpdateJson = objectMapper.writeValueAsString(userToUpdate);

        String errorMessageResponseJson = mockMvc
                .perform(
                        put("/users/{id}", createdUser.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userToUpdateJson)
                )
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorMessageResponse errorMessageResponse = objectMapper
                .readValue(errorMessageResponseJson, ErrorMessageResponse.class);

        String detailedMessage = errorMessageResponse.detailedMessage();

        Assertions.assertEquals(errorMessageResponse.message(), "Request validation failed");
        Assertions.assertTrue(detailedMessage.contains("name:"));
        Assertions.assertTrue(detailedMessage.contains("email:"));
        Assertions.assertTrue(detailedMessage.contains("age:"));
        Assertions.assertNotNull(errorMessageResponse.dateTime());
    }

    @Test
    void shouldFindUserById() throws Exception {
        User userToFind = userService.createUser(
                new User(
                        null,
                        "findMe",
                        "findMe@findMe.com",
                        18,
                        null
                )
        );

        petService.createPet(
                new Pet(
                        null,
                        "Kuzya",
                        userToFind.getId()
                )
        );

        String foundUserJson = mockMvc
                .perform(get("/users/{id}", userToFind.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        User foundUser = objectMapper.readValue(foundUserJson, User.class);

        org.assertj.core.api.Assertions
                .assertThat(foundUser)
                .usingRecursiveComparison()
                .isEqualTo(userToFind);
    }

    @Test
    void shouldNotFindUserByNonExistentId() throws Exception {
        Long nonExistentId = Long.MAX_VALUE;

        String errorMessageResponseJson = mockMvc
                .perform(get("/users/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorMessageResponse errorMessageResponse = objectMapper
                .readValue(errorMessageResponseJson, ErrorMessageResponse.class);

        Assertions.assertEquals(errorMessageResponse.message(), "Entity not found");
        Assertions.assertNotNull(
                errorMessageResponse.detailedMessage(),
                "User with %d not found".formatted(nonExistentId)
        );
        Assertions.assertNotNull(errorMessageResponse.dateTime());
    }

    @Test
    void shouldDeleteUserById() throws Exception {
        User userToDelete = userService.createUser(
                new User(
                        null,
                        "deleteMe",
                        "deleteMe@deleteMe.com",
                        80,
                        null
                )
        );

        Long idUserToDelete = userToDelete.getId();

        mockMvc
                .perform(delete("/users/{id}", idUserToDelete))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(userService.checkIfUserExists(idUserToDelete));
    }

    @Test
    void shouldNotDeleteUserByNonExistentId() throws Exception {
        Long nonExistentId = Long.MAX_VALUE;

        String errorMessageResponseJson = mockMvc
                .perform(delete("/users/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorMessageResponse errorMessageResponse = objectMapper
                .readValue(errorMessageResponseJson, ErrorMessageResponse.class);

        Assertions.assertEquals(errorMessageResponse.message(), "Entity not found");
        Assertions.assertNotNull(
                errorMessageResponse.detailedMessage(),
                "User with %d not found".formatted(nonExistentId)
        );
        Assertions.assertNotNull(errorMessageResponse.dateTime());


        Assertions.assertFalse(userService.checkIfUserExists(nonExistentId));
    }
}