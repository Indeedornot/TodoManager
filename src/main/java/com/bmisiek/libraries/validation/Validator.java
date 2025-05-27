package com.bmisiek.libraries.validation;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Service;

@Service
public class Validator implements ValidatorInterface {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    @Override
    public <T> boolean isValid(T object) {
        var validator = factory.getValidator();
        var violations = validator.validate(object);
        return violations.isEmpty();
    }

    public <T> void assertValid(T object) throws IllegalArgumentException {
        if (!isValid(object)) {
            throw new IllegalArgumentException("Object validation failed");
        }
    }
}
