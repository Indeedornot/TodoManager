package com.bmisiek.libraries.validation;

import org.springframework.stereotype.Service;

@Service
public interface ValidatorInterface {
    <T> boolean isValid(T object);
    <T> void assertValid(T object) throws IllegalArgumentException;
}
