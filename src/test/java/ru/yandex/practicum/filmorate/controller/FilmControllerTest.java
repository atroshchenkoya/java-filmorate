package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldNotCreateFilmWithoutName() throws Exception {
        String filmJson = """
                    {
                        "description": "Funny movie",
                        "releaseDate": "2000-01-01",
                        "duration": 120
                    }
                    """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Название фильма не может быть пустым."));
    }

    @Test
    void shouldNotCreateFilmWithMoreThen200SymbolsDescription() throws Exception {
        String filmJson = "{\n" +
                "  \"name\": \"Movie\",\n" +
                "  \"description\": \"" + "A".repeat(201) + "\",\n" +
                "  \"releaseDate\": \"2000-01-01\",\n" +
                "  \"duration\": 120\n" +
                "}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Описание фильма не может превышать 200 символов."));
    }


    @Test
    void shouldNotCreateFilmWithReleaseDateBefore1895() throws Exception {
        String filmJson = """
                    {
                        "name": "Movie",
                        "description": "Funny movie",
                        "releaseDate": "1895-12-27",
                        "duration": 120
                    }
                    """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.releaseDate").value("Дата релиза фильма не может быть раньше 28 декабря 1895 года."));
    }

    @Test
    void shouldNotCreateFilmWithNegativeDuration() throws Exception {
        String filmJson = """
                        {
                            "name": "Movie",
                            "description": "Funny movie",
                            "releaseDate": "2000-01-01",
                            "duration": -1
                        }
                        """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.duration").value("Продолжительность фильма должна быть положительным числом."));
    }

    @Test
    void shouldCreateFilmSuccessfully() throws Exception {
        String filmJson = "{\n" +
                "  \"name\": \"Movie\",\n" +
                "  \"description\": \"" + "A".repeat(200) + "\",\n" +
                "  \"releaseDate\": \"2000-01-01\",\n" +
                "  \"duration\": 120\n" +
                "}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Movie"))
                .andExpect(jsonPath("$.description").value("A".repeat(200)))
                .andExpect(jsonPath("$.releaseDate").value("2000-01-01"))
                .andExpect(jsonPath("$.duration").value(120));
    }
}
