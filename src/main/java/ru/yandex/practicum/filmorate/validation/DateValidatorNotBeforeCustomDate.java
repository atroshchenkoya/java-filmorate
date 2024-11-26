package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateValidatorNotBeforeCustomDate implements ConstraintValidator<DateValidIfNotBeforeCustomDate, LocalDate> {

    private LocalDate customDate;

    @Override
    public void initialize(DateValidIfNotBeforeCustomDate constraintAnnotation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.customDate = LocalDate.parse(constraintAnnotation.value(), formatter);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(customDate);
    }
}
