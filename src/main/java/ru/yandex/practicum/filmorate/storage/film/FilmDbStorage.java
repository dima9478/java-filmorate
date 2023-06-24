package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT f.*, r.name as rating_name,\n" +
                " array_agg(fg.genre_id) as genre_ids, array_agg(g.name) as genre_names, array_agg(DISTINCT fl.user_id) as likes\n" +
                "FROM films f\n" +
                "    LEFT JOIN film_genres fg ON fg.film_id = f.film_id\n" +
                "    LEFT JOIN genres g ON fg.genre_id = g.genre_id\n" +
                "    LEFT JOIN film_likes fl on f.film_id = fl.film_id\n" +
                "    LEFT JOIN ratings r on f.rating_id = r.rating_id\n" +
                "GROUP BY f.film_id, r.name;";
        return jdbcTemplate.query(sql, (rs, num) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        var releaseDate = rs.getDate("release_date");
        int ratingId = rs.getInt("rating_id");
        Film film = Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(releaseDate == null ? null : releaseDate.toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(ratingId == 0 ? null : new Rating(ratingId, rs.getString("rating_name")))
                .build();
        Object[] genreIds = (Object[]) rs.getArray("genre_ids").getArray();
        Object[] genreNames = (Object[]) rs.getArray("genre_names").getArray();

        for (int i = 0; i < genreIds.length; i++) {
            Object genreId = genreIds[i];
            Object genreName = genreNames[i];

            if (genreId == null) {
                continue;
            }
            film.addGenre(Genre.builder()
                    .id(Integer.parseInt(String.valueOf(genreId)))
                    .name(genreName.toString())
                    .build());
        }

        for (Object like : (Object[]) rs.getArray("likes").getArray()) {
            if (like == null) {
                continue;
            }
            film.addLike(Integer.parseInt(String.valueOf(like)));
        }

        return film;
    }

    @Override
    public Film getById(int id) {
        String sql = "SELECT f.*, r.name as rating_name,\n" +
                " array_agg(fg.genre_id) as genre_ids, array_agg(g.name) as genre_names, array_agg(DISTINCT fl.user_id) as likes\n" +
                "FROM films f\n" +
                "    LEFT JOIN film_genres fg ON fg.film_id = f.film_id\n" +
                "    LEFT JOIN genres g ON fg.genre_id = g.genre_id\n" +
                "    LEFT JOIN film_likes fl on f.film_id = fl.film_id\n" +
                "    LEFT JOIN ratings r on f.rating_id = r.rating_id\n" +
                "WHERE f.film_id = ?\n" +
                "GROUP BY f.film_id, r.name;";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, num) -> makeFilm(rs), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        int filmId = simpleJdbcInsert.executeAndReturnKey(filmToMap(film)).intValue();

        Collection<Integer> genres = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        if (!genres.isEmpty()) {
            addGenres(filmId, genres);
        }

        film.setId(filmId);
        return film;
    }

    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> map = new HashMap<>();
        Rating rating = film.getMpa();
        map.put("name", film.getName());
        map.put("description", film.getDescription());
        map.put("release_date", film.getReleaseDate());
        map.put("duration", film.getDuration());
        map.put("rating_id", rating == null ? null : rating.getId());

        return map;
    }

    private void addGenres(Integer filmId, Collection<Integer> genres) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?);";
        for (Integer genre : genres) {
            jdbcTemplate.update(sql, filmId, genre);
        }
    }

    private void removeGenres(Integer filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public Film update(Film film) {
        Integer filmId = film.getId();
        Collection<Integer> genres = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());

        String sql = "UPDATE films SET\n" +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                "WHERE film_id = ?";
        Rating rating = film.getMpa();
        int flag = jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                rating == null ? null : rating.getId(),
                film.getId());
        if (flag == 0) {
            return null;
        }

        removeGenres(filmId);
        if (!genres.isEmpty()) {
            addGenres(filmId, genres);
        }
        return getById(filmId);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, r.name as rating_name,\n" +
                " array_agg(fg.genre_id) as genre_ids, array_agg(g.name) as genre_names, array_agg(DISTINCT fl.user_id) as likes\n" +
                "FROM films f\n" +
                "    LEFT JOIN film_genres fg ON fg.film_id = f.film_id\n" +
                "    LEFT JOIN genres g ON fg.genre_id = g.genre_id\n" +
                "    LEFT JOIN film_likes fl on f.film_id = fl.film_id\n" +
                "    LEFT JOIN ratings r on f.rating_id = r.rating_id\n" +
                "GROUP BY f.film_id, r.name\n" +
                "ORDER BY array_length(array_agg(DISTINCT fl.user_id) FILTER (WHERE fl.user_id is not null)) DESC\n" +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, (rs, num) -> makeFilm(rs), count);
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }
}
