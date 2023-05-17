package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidatorTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Film film;

    @BeforeEach
    public void setUp() {
        film = new Film();
        film.setDuration(100);
        film.setDescription("desc");
        film.setName("Name");
        film.setReleaseDate(LocalDate.of(1900, 10, 12));
    }

    @Test
    public void checkSuccessValidation() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void checkNonPositiveDuration() {
        film.setDuration(-100);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(violations.size(), 1);

        film.setDuration(0);
        violations = validator.validate(film);
        assertEquals(violations.size(), 1);
    }

    @Test
    public void checkBlankName() {
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(violations.size(), 1);

        film.setName(null);
        violations = validator.validate(film);
        assertEquals(violations.size(), 1);
    }

    @Test
    public void checkMaxDescriptionLengthBiggerThan200() {
        film.setDescription("a".repeat(300));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(violations.size(), 1);

        film.setDescription("d".repeat(200));
        violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void checkReleaseDateBefore1895() {
        film.setReleaseDate(LocalDate.of(1880, 1, 12));

        assertFalse(FilmValidator.isValid(film));
    }

}
