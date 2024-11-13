package dev.vudovenko.springbootmvcpractice.exceptionHandling.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EmptyListValidator.class)
public @interface EmptyList {

    String message() default "The pets list must be null or empty upon user creation";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}