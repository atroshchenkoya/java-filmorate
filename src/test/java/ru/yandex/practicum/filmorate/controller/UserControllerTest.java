package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldNotCreateUserWithoutEmail() throws Exception {
        String userJson = """
                {
                    "login": "test_user",
                    "name": "Alexey Popov",
                    "birthday": "2000-01-01"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Электронная почта не может быть пустой."));
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() throws Exception {
        String userJson = """
                {
                    "email": "invalid-email",
                    "login": "test_user",
                    "name": "Alexey Popov",
                    "birthday": "2000-01-01"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Электронная почта должна быть валидной."));
    }

    @Test
    void shouldNotCreateUserWithEmptyLogin() throws Exception {
        String userJson = """
                {
                    "email": "alex@gmail.com",
                    "login": " ",
                    "name": "Alexey Popov",
                    "birthday": "2000-01-01"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.login").value("Логин не может быть пустым или содержать пробелы."));
    }

    @Test
    void shouldNotCreateUserWithFutureBirthday() throws Exception {
        String userJson = """
                {
                    "email": "alex@gmail.com",
                    "login": "test_user",
                    "name": "Alexey Popov",
                    "birthday": "2050-01-01"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.birthday").value("Дата рождения не может быть в будущем."));
    }

    @Test
    void shouldUseLoginAsNameIfNameIsEmpty() throws Exception {
        String userJson = """
                {
                    "email": "alex@gmail.com",
                    "login": "test_user",
                    "name": "",
                    "birthday": "2000-01-01"
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test_user"));
    }
}
