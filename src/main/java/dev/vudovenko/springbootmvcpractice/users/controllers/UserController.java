package dev.vudovenko.springbootmvcpractice.users.controllers;

import dev.vudovenko.springbootmvcpractice.users.model.User;
import dev.vudovenko.springbootmvcpractice.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("Create user: {}", user);
        User createdUser = userService.createUser(user);

        return ResponseEntity
                .status(201)
                .body(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        log.info("Get user with id: {}", id);
        User user = userService.getById(id);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user
    ) {
        log.info("Update user with id: {}, user: {}", id, user);
        User updatedUser = userService.updateUser(id, user);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Delete user with id: {}", id);
        userService.deleteUser(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
