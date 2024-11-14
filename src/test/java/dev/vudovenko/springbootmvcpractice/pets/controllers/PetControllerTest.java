package dev.vudovenko.springbootmvcpractice.pets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vudovenko.springbootmvcpractice.exceptionHandling.dto.ErrorMessageResponse;
import dev.vudovenko.springbootmvcpractice.pets.model.Pet;
import dev.vudovenko.springbootmvcpractice.pets.services.PetService;
import dev.vudovenko.springbootmvcpractice.users.model.User;
import dev.vudovenko.springbootmvcpractice.users.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User(
                null,
                "owner",
                "owner@owner.com",
                24,
                new ArrayList<>()
        );
        owner = userService.createUser(owner);
    }

    @Test
    void shouldSuccessCreatePet() throws Exception {
        Pet petToCreate = new Pet(
                null,
                "petToCreate",
                owner.getId()
        );

        String petToCreateJson = objectMapper.writeValueAsString(petToCreate);

        String createdPetJson = mockMvc
                .perform(
                        post("/pets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(petToCreateJson)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Pet createdPet = objectMapper.readValue(createdPetJson, Pet.class);

        Assertions.assertNotNull(createdPet.getId());
        org.assertj.core.api.Assertions
                .assertThat(createdPet)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(petToCreate);
    }

    @Test
    void shouldNotCreatePetWhenUserNotExists() throws Exception {
        Pet petToCreate = new Pet(
                null,
                "petToCreate",
                Long.MAX_VALUE
        );

        String petToCreateJson = objectMapper.writeValueAsString(petToCreate);

        String errorMessageResponseJson = mockMvc
                .perform(
                        post("/pets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(petToCreateJson)
                )
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorMessageResponse errorMessageResponse = objectMapper
                .readValue(errorMessageResponseJson, ErrorMessageResponse.class);

        Assertions.assertEquals(errorMessageResponse.message(), "Entity not found");
        Assertions.assertEquals(errorMessageResponse.detailedMessage(),
                "Owner with %d not found".formatted(petToCreate.getUserId()));
        Assertions.assertNotNull(errorMessageResponse.dateTime());
    }

    @Test
    void shouldNotCreatePetWhenRequestNotValid() throws Exception {
        Pet petToCreate = new Pet(
                Long.MAX_VALUE,
                "       ",
                null
        );

        String petToCreateJson = objectMapper.writeValueAsString(petToCreate);

        String errorMessageResponseJson = mockMvc
                .perform(
                        post("/pets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(petToCreateJson)
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
        Assertions.assertTrue(detailedMessage.contains("userId:"));
        Assertions.assertNotNull(errorMessageResponse.dateTime());
    }

    @Test
    void shouldSuccessUpdatePetWithOldOwner() throws Exception {
        Pet createdPet = petService.createPet(
                new Pet(
                        null,
                        "petToCreate",
                        owner.getId()
                )
        );

        Pet petToUpdate = new Pet(
                null,
                "petToUpdate",
                owner.getId()
        );

        String petToUpdateJson = objectMapper.writeValueAsString(petToUpdate);

        String updatedPetJson = mockMvc
                .perform(
                        put("/pets/{id}", createdPet.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(petToUpdateJson)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Pet updatedPet = objectMapper.readValue(updatedPetJson, Pet.class);

        Assertions.assertEquals(updatedPet.getId(), createdPet.getId());
        org.assertj.core.api.Assertions
                .assertThat(updatedPet)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(petToUpdate);
    }

    @Test
    void shouldSuccessUpdatePetWithNewOwner() throws Exception {
        Pet createdPet = petService.createPet(
                new Pet(
                        null,
                        "petToCreate",
                        owner.getId()
                )
        );

        User newOwner = userService.createUser(
                new User(
                        null,
                        "newOwner",
                        "newOwner@newOwner.com",
                        99,
                        null
                )
        );

        Pet petToUpdate = new Pet(
                null,
                "petToUpdate",
                newOwner.getId()
        );

        String petToUpdateJson = objectMapper.writeValueAsString(petToUpdate);

        String updatedPetJson = mockMvc
                .perform(
                        put("/pets/{id}", createdPet.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(petToUpdateJson)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Pet updatedPet = objectMapper.readValue(updatedPetJson, Pet.class);

        Assertions.assertEquals(updatedPet.getId(), createdPet.getId());
        org.assertj.core.api.Assertions
                .assertThat(updatedPet)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(petToUpdate);
    }

    @Test
    void shouldNotUpdatePetWhenPetIdNotExists() throws Exception {
        Pet petToUpdate = new Pet(
                null,
                "petToUpdate",
                owner.getId()
        );

        String petToUpdateJson = objectMapper.writeValueAsString(petToUpdate);

        Long nonExistentPetId = Long.MAX_VALUE;

        String errorMessageResponseJson = mockMvc
                .perform(
                        put("/pets/{id}", nonExistentPetId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(petToUpdateJson)
                )
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorMessageResponse errorMessageResponse = objectMapper
                .readValue(errorMessageResponseJson, ErrorMessageResponse.class);

        Assertions.assertEquals(errorMessageResponse.message(), "Entity not found");
        Assertions.assertEquals(errorMessageResponse.detailedMessage(),
                "Pet with %d not found".formatted(nonExistentPetId));
        Assertions.assertNotNull(errorMessageResponse.dateTime());
    }

    @Test
    void shouldNotUpdatePetWhenRequestNotValid() throws Exception {
        Pet createdPet = petService.createPet(
                new Pet(
                        null,
                        "Valid name",
                        owner.getId()
                )
        );

        Pet invalidPetToUpdate = new Pet(
                Long.MAX_VALUE,
                "as",
                null
        );

        String invalidPetJson = objectMapper.writeValueAsString(invalidPetToUpdate);

        String errorMessageResponseJson = mockMvc
                .perform(
                        put("/pets/{id}", createdPet.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidPetJson)
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
        Assertions.assertTrue(detailedMessage.contains("userId:"));
        Assertions.assertNotNull(errorMessageResponse.dateTime());
    }

    @Test
    void shouldFindPetById() throws Exception {
        Pet createdPet = petService.createPet(
                new Pet(
                        null,
                        "Valid name",
                        owner.getId()
                )
        );

        String foundPetJson = mockMvc
                .perform(get("/pets/{id}", createdPet.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Pet foundPet = objectMapper.readValue(foundPetJson, Pet.class);

        org.assertj.core.api.Assertions
                .assertThat(foundPet)
                .usingRecursiveComparison()
                .isEqualTo(createdPet);
    }

    @Test
    void shouldNotFindPetByNonExistentId() throws Exception {
        Long nonExistentId = Long.MAX_VALUE;

        String errorMessageResponseJson = mockMvc
                .perform(get("/pets/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorMessageResponse errorMessageResponse = objectMapper
                .readValue(errorMessageResponseJson, ErrorMessageResponse.class);

        Assertions.assertEquals(errorMessageResponse.message(), "Entity not found");
        Assertions.assertEquals(errorMessageResponse.detailedMessage(),
                "Pet with %d not found".formatted(nonExistentId));
        Assertions.assertNotNull(errorMessageResponse.dateTime());
    }

    @Test
    void shouldDeletePetById() throws Exception {
        Pet petToDelete = petService.createPet(
                new Pet(
                        null,
                        "Pet to delete",
                        owner.getId()
                )
        );

        mockMvc
                .perform(delete("/pets/{id}", petToDelete.getId()))
                .andExpect(status().isNoContent());

        Assertions.assertFalse(petService.checkIfPetExists(petToDelete.getId()));
    }

    @Test
    void shouldNotDeletePetByNonExistentId() throws Exception {
        Long nonExistentPetId = Long.MAX_VALUE;

        String errorMessageResponseJson = mockMvc
                .perform(delete("/pets/{id}", nonExistentPetId))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorMessageResponse errorMessageResponse = objectMapper
                .readValue(errorMessageResponseJson, ErrorMessageResponse.class);

        Assertions.assertEquals(errorMessageResponse.message(), "Entity not found");
        Assertions.assertEquals(errorMessageResponse.detailedMessage(),
                "Pet with %d not found".formatted(nonExistentPetId));
        Assertions.assertNotNull(errorMessageResponse.dateTime());
    }
}