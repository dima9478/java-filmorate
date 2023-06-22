package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("userDbStorage")
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> getAll() {
        String sql = "SELECT u.*, array_agg(f.friend_id) as friends FROM users u\n" +
                "LEFT JOIN friendships f ON u.user_id = f.user_id\n" +
                "GROUP BY u.user_id";
        return jdbcTemplate.query(sql, (rs, num) -> makeUser(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        var birthday = rs.getDate("birthday");
        User user = User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(birthday == null ? null : birthday.toLocalDate())
                .build();

        for (Object friend : (Object[]) rs.getArray("friends").getArray()) {
            if (friend == null) {
                continue;
            }
            user.addFriend(Integer.parseInt(String.valueOf(friend)));
        }
        return user;
    }

    @Override
    public User getById(int id) {
        String sql = "SELECT u.*, array_agg(f.friend_id) as friends FROM users u\n" +
                "LEFT JOIN friendships f ON u.user_id = f.user_id\n" +
                "WHERE u.user_id = ?\n" +
                "GROUP BY u.user_id";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, num) -> makeUser(rs), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        int userId = simpleJdbcInsert.executeAndReturnKey(userToMap(user)).intValue();

        user.setId(userId);
        return user;
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", user.getEmail());
        map.put("login", user.getLogin());
        map.put("name", user.getName());
        map.put("birthday", user.getBirthday());

        return map;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET\n" +
                "email = ?, login = ?, name = ?, birthday = ?\n" +
                "WHERE user_id = ?";
        int flag = jdbcTemplate.update(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return flag > 0 ? user : null;
    }

    @Override
    public Collection<User> getFriends(int userId) {
        String sql = "SELECT u.*, array_agg(f2.friend_id) as friends FROM friendships f\n" +
                "    INNER JOIN users u ON u.user_id = f.friend_id\n" +
                "    LEFT JOIN friendships f2 ON u.user_id = f2.user_id\n" +
                "WHERE f.user_id = ?\n" +
                "GROUP BY u.user_id;";
        return jdbcTemplate.query(sql, (rs, num) -> makeUser(rs), userId);
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int otherId) {
        String sql = "SELECT u.*, array_agg(f.friend_id) as friends FROM users u\n" +
                "    LEFT JOIN friendships f ON u.user_id = f.user_id\n" +
                "WHERE u.user_id IN (\n" +
                "    SELECT friend_id FROM friendships\n" +
                "    WHERE user_id = ?\n" +
                "    INTERSECT\n" +
                "    SELECT friend_id FROM friendships\n" +
                "    WHERE user_id = ?\n" +
                ")\n" +
                "GROUP BY u.user_id;";
        return jdbcTemplate.query(sql, (rs, num) -> makeUser(rs), userId, otherId);
    }

    @Override
    public void addFriend(int userId, int otherId) {
        String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, otherId);
    }

    @Override
    public void deleteFriend(int userId, int otherId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, otherId);
    }
}
