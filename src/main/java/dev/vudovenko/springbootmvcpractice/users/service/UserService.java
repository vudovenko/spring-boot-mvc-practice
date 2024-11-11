package dev.vudovenko.springbootmvcpractice.users.service;

import dev.vudovenko.springbootmvcpractice.users.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private Long idCounter;

    private final Map<Long, User> users;

    public UserService() {
        this.idCounter = 0L;
        this.users = new HashMap<>();
    }

    private Long getNextId() {
        return ++idCounter;
    }

    public User getById(Long id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
    }

    public Boolean checkIfUserExists(Long id) {
        return users.containsKey(id);
    }
}
