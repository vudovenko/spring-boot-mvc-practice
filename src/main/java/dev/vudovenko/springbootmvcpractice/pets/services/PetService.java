package dev.vudovenko.springbootmvcpractice.pets.services;

import dev.vudovenko.springbootmvcpractice.pets.model.Pet;
import dev.vudovenko.springbootmvcpractice.users.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PetService {

    private Long idCounter;
    private final Map<Long, Pet> pets;

    public PetService(UserService userService) {
        this.idCounter = 0L;
        this.pets = new HashMap<>();
    }

    private Long getNextId() {
        return ++idCounter;
    }

    public Pet createPet(Pet pet) {
        if (pet.getId() != null) {
            throw new IllegalArgumentException("Id must be null");
        }

        pet.setId(getNextId());
        pets.put(pet.getId(), pet);

        return pet;
    }

    public Pet getPetById(Long id) {
        return Optional.ofNullable(pets.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Pet with id " + id + " not found"));
    }

    public Pet updatePet(Long id, Pet petToUpdate) {
        if (!checkIfPetExists(id)) {
            throw new IllegalArgumentException("Pet with id " + id + " not found");
        }

        petToUpdate.setId(id);
        pets.put(id, petToUpdate);

        return petToUpdate;
    }

    public void deletePet(Long id) {
        pets.remove(id);
    }

    public Boolean checkIfPetExists(Long id) {
        return pets.containsKey(id);
    }
}
