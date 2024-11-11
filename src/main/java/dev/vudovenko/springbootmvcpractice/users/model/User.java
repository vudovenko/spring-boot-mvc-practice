package dev.vudovenko.springbootmvcpractice.users.model;

import dev.vudovenko.springbootmvcpractice.pets.model.Pet;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Null
    private Long id;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer age;

    @Valid
    private List<Pet> pets;
}
