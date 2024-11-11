package dev.vudovenko.springbootmvcpractice.pets.controllers;

import dev.vudovenko.springbootmvcpractice.pets.model.Pet;
import dev.vudovenko.springbootmvcpractice.pets.services.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    @PostMapping
    public ResponseEntity<Pet> createPet(@Valid @RequestBody Pet pet) {
        log.info("Create pet: {}", pet);
        Pet createdPet = petService.createPet(pet);

        return ResponseEntity
                .status(201)
                .body(createdPet);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPet(@PathVariable Long id) {
        log.info("Get pet with id: {}", id);
        Pet pet = petService.getPetById(id);

        return ResponseEntity.ok(pet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> updatePet(
            @PathVariable Long id,
            @Valid @RequestBody Pet pet
    ) {
        log.info("Update pet with id: {}, pet: {}", id, pet);
        Pet updatedPet = petService.updatePet(id, pet);

        return ResponseEntity.ok(updatedPet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        log.info("Delete pet with id: {}", id);
        petService.deletePet(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
