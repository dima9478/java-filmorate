package ru.yandex.practicum.filmorate.storage.rating;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@AllArgsConstructor
public class RatingDbStorage implements RatingStorage {
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Rating> getAll() {
        String sql = "SELECT * FROM ratings ORDER BY rating_id";
        return jdbcTemplate.query(sql, (rs, num) -> makeRating(rs));
    }

    private Rating makeRating(ResultSet rs) throws SQLException {
        return Rating.builder()
                .id(rs.getInt("rating_id"))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public Rating getById(int id) {
        String sql = "SELECT * FROM ratings WHERE rating_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, num) -> makeRating(rs), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
