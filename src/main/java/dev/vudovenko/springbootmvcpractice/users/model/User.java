package dev.vudovenko.springbootmvcpractice.users.model;

import dev.vudovenko.springbootmvcpractice.exceptionHandling.validator.EmptyList;
import dev.vudovenko.springbootmvcpractice.pets.model.Pet;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
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

    @EmptyList
    private List<Pet> pets;
}
