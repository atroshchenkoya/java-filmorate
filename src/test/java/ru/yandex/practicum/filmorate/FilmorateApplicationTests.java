package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserMapper.class})
class FilmorateApplicationTests {

	private final UserDbStorage userStorage;
	private final JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUp() {
		String sql = "INSERT INTO users (id, email, login, name, birthday) " +
				"VALUES (1, 'test@example.com', 'test_user', 'Test User', '2000-01-01')";
		jdbcTemplate.update(sql);
	}

	@Test
	public void testFindUserById() {
		Optional<User> userOptional = userStorage.findById(1L);

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user -> {
					assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
					assertThat(user).hasFieldOrPropertyWithValue("email", "test@example.com");
					assertThat(user).hasFieldOrPropertyWithValue("login", "test_user");
					assertThat(user).hasFieldOrPropertyWithValue("name", "Test User");
					assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 1, 1));
				});
	}
}
