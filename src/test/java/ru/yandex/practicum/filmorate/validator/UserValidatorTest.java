package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("ivan@mail.ru");
        user.setLogin("login23");
        user.setName(null);
        user.setBirthday(LocalDate.of(1996, 10, 12));
    }

    @Test
    public void checkSuccessfulValidation() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void checkInvalidEmail() {
        user.setEmail("dfg@");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);

        user.setEmail("mail");
        violations = validator.validate(user);
        assertEquals(violations.size(), 1);

        user.setEmail("");
        violations = validator.validate(user);
        assertEquals(violations.size(), 1);

        user.setEmail(null);
        violations = validator.validate(user);
        assertEquals(violations.size(), 1);
    }

    @Test
    public void checkInvalidLogin() {
        user.setLogin("df  yu");
        assertFalse(UserValidator.isValid(user));

        user.setLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);

        user.setLogin(null);
        violations = validator.validate(user);
        assertEquals(violations.size(), 1);
    }

    @Test
    public void checkNullableName() {
        user.setName(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());

        user.setName("");
        violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void checkBirthdayInFuture() {
        user.setBirthday(LocalDate.of(1_000_000, 10, 12));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(violations.size(), 1);
    }
}
