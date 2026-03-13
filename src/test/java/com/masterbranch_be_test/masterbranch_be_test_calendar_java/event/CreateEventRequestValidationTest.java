package com.masterbranch_be_test.masterbranch_be_test_calendar_java.event;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.masterbranch_be_test.masterbranch_be_test_calendar_java.event.dto.CreateEventRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class CreateEventRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldFailWhenRequiredFieldsAreMissing() {
        CreateEventRequest request = new CreateEventRequest();

        Set<ConstraintViolation<CreateEventRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }
}
