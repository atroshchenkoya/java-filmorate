package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final GenreMapper genreMapper;
    private final MpaMapper mpaMapper;

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

        if (films.isEmpty()) {
            return films;
        }

        String genreSql = "SELECT fg.film_id, g.id AS genre_id, g.name AS genre_name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id IN (%s)";

        String filmIds = films.stream()
                .map(film -> String.valueOf(film.getId()))
                .collect(Collectors.joining(","));

        Map<Long, Set<Genre>> filmGenres = getGenresForFilms(genreSql, filmIds);

        films.forEach(film -> film.setGenres(filmGenres.getOrDefault(film.getId(), new HashSet<>())));

        Map<Long, Mpa> filmMpa = getMpaForFilms(films);

        films.forEach(film -> film.setMpa(filmMpa.get(film.getId())));

        return films;
    }

    private Map<Long, Set<Genre>> getGenresForFilms(String genreSql, String filmIds) {
        return jdbcTemplate.query(
                String.format(genreSql, filmIds),
                rs -> {
                    Map<Long, Set<Genre>> genresMap = new HashMap<>();
                    while (rs.next()) {
                        long filmId = rs.getLong("film_id");
                        Genre genre = genreMapper.mapRow(rs, rs.getRow());
                        genresMap.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
                    }
                    return genresMap;
                }
        );
    }

    private Map<Long, Mpa> getMpaForFilms(List<Film> films) {
        if (films.isEmpty()) {
            return Collections.emptyMap();
        }

        String sql = "SELECT f.id AS film_id, m.id AS mpa_id, m.name AS mpa_name, m.description AS mpa_description " +
                "FROM films f " +
                "JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
                "WHERE f.id IN (:filmIds)";

        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        Map<String, Object> params = new HashMap<>();
        params.put("filmIds", filmIds);

        return namedParameterJdbcTemplate.query(sql, params, rs -> {
            Map<Long, Mpa> mpaMap = new HashMap<>();
            while (rs.next()) {
                long filmId = rs.getLong("film_id");
                Mpa mpa = mpaMapper.mapRow(rs, rs.getRow());
                mpaMap.put(filmId, mpa);
            }
            return mpaMap;
        });
    }

    private boolean mpaExists(Long mpaId) {
        String sql = "SELECT COUNT(*) FROM mpa_ratings WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, mpaId);
        return count <= 0;
    }

    private List<Long> getExistingGenreIds(List<Long> genreIds) {
        String sql = "SELECT id FROM genres WHERE id IN (:genreIds)";

        Map<String, Object> params = new HashMap<>();
        params.put("genreIds", genreIds);

        return namedParameterJdbcTemplate.queryForList(sql, params, Long.class);
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

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Long> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toList());

            List<Long> existingGenreIds = getExistingGenreIds(genreIds);

            for (Genre genre : film.getGenres()) {
                if (!existingGenreIds.contains(genre.getId())) {
                    throw new ConditionsNotMetException("Жанр с id = " + genre.getId() + " не найден.");
                }
            }
        }
    }

    private void updateGenres(long filmId, Set<Genre> genres) {
        String deleteSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, filmId);

        if (genres != null && !genres.isEmpty()) {
            String insertSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

            List<Object[]> batchArgs = new ArrayList<>();
            for (Genre genre : genres) {
                batchArgs.add(new Object[]{filmId, genre.getId()});
            }

            jdbcTemplate.batchUpdate(insertSql, batchArgs);
        }
    }

    private Set<Genre> getGenresByFilmId(Long filmId) {
        String sql = "SELECT g.id, g.name FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, genreMapper, filmId));
    }

    private Mpa getMpaByFilmId(Long filmId) {
        String sql = "SELECT m.id, m.name, m.description FROM mpa_ratings m JOIN films f ON m.id = f.MPA_RATING_ID WHERE f.id = ?";
        return jdbcTemplate.queryForObject(sql, mpaMapper, filmId);
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
}
