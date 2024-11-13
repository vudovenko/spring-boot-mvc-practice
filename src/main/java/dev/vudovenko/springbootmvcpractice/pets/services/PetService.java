package dev.vudovenko.springbootmvcpractice.pets.services;

import dev.vudovenko.springbootmvcpractice.exceptionHandling.exceptions.PetNotFoundException;
import dev.vudovenko.springbootmvcpractice.exceptionHandling.exceptions.UserNotFoundException;
import dev.vudovenko.springbootmvcpractice.managingID.IDManager;
import dev.vudovenko.springbootmvcpractice.pets.model.Pet;
import dev.vudovenko.springbootmvcpractice.users.model.User;
import dev.vudovenko.springbootmvcpractice.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Validated
@Service
public class PetService extends IDManager {

    private final Map<Long, Pet> pets;

    private final UserService userService;

    public PetService(@Lazy UserService userService) {
        this.pets = new HashMap<>();
        this.userService = userService;
    }

    public Pet createPet(@Valid Pet pet) {
        if (!userService.checkIfUserExists(pet.getUserId())) {
            throw new UserNotFoundException("Owner with id " + pet.getUserId() + " not found");
        }

        pet.setId(getNextId());
        pets.put(pet.getId(), pet);

        userService.addPetToUser(pet.getUserId(), pet);

        return pet;
    }

    public Pet getPetById(Long id) {
        return Optional.ofNullable(pets.get(id))
                .orElseThrow(() -> new PetNotFoundException("Pet with id " + id + " not found"));
    }

    public Pet updatePet(Long petId, @Valid Pet petToUpdate) {
        Pet oldPet = getPetById(petId);
        oldPet.setName(petToUpdate.getName());
        removePetFromOwner(petId);

        Long newOwnerId = petToUpdate.getUserId();
        if (!userService.checkIfUserExists(newOwnerId)) {
            throw new UserNotFoundException("Owner with id " + newOwnerId + " not found");
        }
        userService.addPetToUser(newOwnerId, oldPet);

        return oldPet;
    }

    public void deletePet(Long id) {
        removePetFromOwner(id);
        pets.remove(id);
    }

    public void removePetFromOwner(Long petId) {
        Pet pet = getPetById(petId);
        User oldOwner = userService.getById(pet.getUserId());
        oldOwner.getPets().remove(pet);
        pet.setUserId(null);
    }
}
