package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private final UserController userController = new UserController();

    @Test
    void shouldNotCreateUserWithoutEmail() {
        User user = new User();
        user.setLogin("test_user");
        user.setName("Alexey Popov");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> userController.create(user));

        assertEquals("Некорректная электронная почта.", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("test_user");
        user.setName("Alexey Popov");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> userController.create(user));

        assertEquals("Некорректная электронная почта.", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithEmptyLogin() {
        User user = new User();
        user.setEmail("alex@gmail.com");
        user.setLogin(" ");
        user.setName("Alexey Popov");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> userController.create(user));

        assertEquals("Логин не может быть пустым или содержать пробелы.", exception.getMessage());
    }

    @Test
    void shouldNotCreateUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("alex@gmail.com");
        user.setLogin("test_user");
        user.setName("Alexey Popov");
        user.setBirthday(LocalDate.now().plusDays(1));

        Exception exception = assertThrows(ConditionsNotMetException.class, () -> userController.create(user));

        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }

    @Test
    void shouldUseLoginAsNameIfNameIsEmpty() {
        User user = new User();
        user.setEmail("alex@gmail.com");
        user.setLogin("test_user");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.create(user);

        assertEquals("test_user", createdUser.getName());
    }
}
