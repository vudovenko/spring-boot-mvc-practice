package dev.vudovenko.springbootmvcpractice.exceptionHandling.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class EmptyListValidator implements ConstraintValidator<EmptyList, Collection<?>> {

    @Override
    public void initialize(EmptyList constraintAnnotation) {
    }

    @Override
    public boolean isValid(Collection<?> collection, ConstraintValidatorContext context) {
        return collection == null || collection.isEmpty();
    }
}