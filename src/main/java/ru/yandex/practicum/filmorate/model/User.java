package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "email")
public class User {
    private long id;

    private Set<Long> friends = new HashSet<>();

    @NotBlank(message = "Электронная почта не может быть пустой.")
    @Email(message = "Электронная почта должна быть валидной.")
    private String email;

    @NotBlank(message = "Логин не может быть пустым или содержать пробелы.")
    @Pattern(regexp = "^\\S*$", message = "Логин не может быть пустым или содержать пробелы.")
    private String login;

    private String name;

    @Past(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;
}