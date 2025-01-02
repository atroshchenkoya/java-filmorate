package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Film> findById(Long id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
            assert film != null;
            film.setGenres(getGenresByFilmId(id));
            film.setMpa(getMpaByFilmId(id));
            return Optional.of(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);
        films.forEach(film -> {
            film.setGenres(getGenresByFilmId(film.getId()));
            film.setMpa(getMpaByFilmId(film.getId()));
        });
        return films;
    }

    @Override
    public boolean likeExists(Film film, User user) {
        String sql = "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, film.getId(), user.getId());
        return count > 0;
    }

    @Override
    public void removeLike(Film film, User user) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, film.getId(), user.getId());
    }

    @Override
    public void addLike(Film film, User user) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, film.getId(), user.getId());
    }

    private boolean mpaExists(Long mpaId) {
        String sql = "SELECT COUNT(*) FROM mpa_ratings WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, mpaId);
        return count <= 0;
    }

    private boolean genreExists(Long genreId) {
        String sql = "SELECT COUNT(*) FROM genres WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, genreId);
        return count <= 0;
    }

    private boolean filmExists(Long filmId) {
        String sql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId);
        return count > 0;
    }

    @Override
    public Film create(Film film) {

        checkCorrectMpaAndGenres(film);

        String sql = "INSERT INTO films (name, description, release_date, duration, MPA_RATING_ID) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(filmId);
        updateGenres(filmId, film.getGenres());
        return film;
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
                "COUNT(fl.user_id) AS like_count " +
                "FROM films f " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    @Override
    public Film update(Film film) {
        if (!filmExists(film.getId())) {
            throw new ConditionsNotMetException("Фильм с id = " + film.getId() + " не найден.");
        }

        checkCorrectMpaAndGenres(film);

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, MPA_RATING_ID = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getMpa().getId(), film.getId());

        updateGenres(film.getId(), film.getGenres());

        return findById(film.getId()).orElse(null);
    }

    private void checkCorrectMpaAndGenres(Film film) {
        if (film.getMpa() == null || mpaExists(film.getMpa().getId())) {
            assert film.getMpa() != null;
            throw new ConditionsNotMetException("Рейтинг MPA с id = " + film.getMpa().getId() + " не найден.");
        }

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genreExists(genre.getId())) {
                    throw new ConditionsNotMetException("Жанр с id = " + genre.getId() + " не найден.");
                }
            }
        }
    }

    private void updateGenres(long filmId, Set<Genre> genres) {
        String deleteSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, filmId);

        String insertSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        if (genres != null) {
            for (Genre genre : genres) {
                jdbcTemplate.update(insertSql, filmId, genre.getId());
            }
        }
    }

    private Set<Genre> getGenresByFilmId(Long filmId) {
        String sql = "SELECT g.id, g.name FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, this::mapRowToGenre, filmId));
    }

    private Mpa getMpaByFilmId(Long filmId) {
        String sql = "SELECT m.id, m.name, m.DESCRIPTION FROM mpa_ratings m JOIN films f ON m.id = f.MPA_RATING_ID WHERE f.id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToMpa, filmId);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                null,
                null
        );
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getLong("id"), rs.getString("name"));
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description")
        );
    }
}
