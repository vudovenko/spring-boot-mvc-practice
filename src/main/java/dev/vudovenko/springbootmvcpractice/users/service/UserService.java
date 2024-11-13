package dev.vudovenko.springbootmvcpractice.users.service;

import dev.vudovenko.springbootmvcpractice.exceptionHandling.exceptions.UserNotFoundException;
import dev.vudovenko.springbootmvcpractice.managingID.IDManager;
import dev.vudovenko.springbootmvcpractice.pets.model.Pet;
import dev.vudovenko.springbootmvcpractice.pets.services.PetService;
import dev.vudovenko.springbootmvcpractice.users.model.User;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Validated
@Service
public class UserService extends IDManager {

    private final Map<Long, User> users;

    private final PetService petService;

    public UserService(PetService petService) {
        this.users = new HashMap<>();
        this.petService = petService;
    }

    public User createUser(@Valid User user) {
        user.setId(getNextId());
        user.setPets(new ArrayList<>());
        users.put(user.getId(), user);

        return user;
    }

    public User getById(Long id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new UserNotFoundException("User with %d not found".formatted(id)));
    }

    public User updateUser(Long id, @Valid User user) {
        User oldUser = getById(id);
        oldUser.setName(user.getName());
        oldUser.setEmail(user.getEmail());
        oldUser.setAge(user.getAge());

        return oldUser;
    }

    public void deleteUser(Long id) {
        User user = getById(id);
        user.getPets()
                .forEach(pet -> petService.deletePet(pet.getId()));
        users.remove(id);
    }

    public Boolean checkIfUserExists(Long id) {
        return users.containsKey(id);
    }

    public void addPetToUser(Long userId, Pet pet) {
        User user = getById(userId);
        user.getPets().add(pet);

        pet.setUserId(userId);
    }
}
