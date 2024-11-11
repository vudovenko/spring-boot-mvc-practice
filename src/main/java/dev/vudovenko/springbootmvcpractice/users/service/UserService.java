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

    public User createUser(User user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("Id must be null");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    public User getById(Long id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
    }

    public User updateUser(Long id, User user) {
        if (!checkIfUserExists(id)) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }

        user.setId(id);
        users.put(id, user);

        return user;
    }

    public void deleteUser(Long id) {
        users.remove(id);
    }

    public Boolean checkIfUserExists(Long id) {
        return users.containsKey(id);
    }
}
