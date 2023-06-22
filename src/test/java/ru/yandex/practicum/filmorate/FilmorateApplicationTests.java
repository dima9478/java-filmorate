package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;
	private final RatingDbStorage ratingStorage;
	private final GenreDbStorage genreStorage;
	User user;
	User otherUser;
	Film film;

	@Test
	void contextLoads() {
	}

	@BeforeEach
	public void setUp() {
		user = User.builder()
				.name("Sergio")
				.email("se@ya.ru")
				.login("serg")
				.build();
		otherUser = User.builder()
				.name("Ivan")
				.email("vva@ya.ru")
				.login("van")
				.build();
		film = Film.builder()
				.name("Film")
				.description("Gripping")
				.duration(120)
				.build();
	}

	@Test
	public void testFindUserById() {
		User storedUser = userStorage.create(user);
		User user2 = userStorage.getById(storedUser.getId());

		assertNotNull(user2);
		assertEquals(storedUser.getId(), user2.getId());
		assertEquals(storedUser, user2);
	}

	@Test
	public void testFindUserByWrongId() {
		User user2 = userStorage.getById(1000);
		assertNull(user2);
	}

	@Test
	public void testFindAllUsers() {
		userStorage.create(user);
		user.setEmail("re@ya.ru");
		userStorage.create(user);

		Collection<User> users = userStorage.getAll();
		assertFalse(users.isEmpty());
		assertEquals(users.size(), 2);
	}

	@Test
	public void testUpdateUser() {
		User storedUser = userStorage.create(user);
		storedUser.setEmail("new_email");
		userStorage.update(storedUser);

		User updatedUser = userStorage.getById(storedUser.getId());
		assertEquals(updatedUser.getId(), storedUser.getId());
		assertEquals(updatedUser.getEmail(), "new_email");
	}

	@Test
	public void testAddFriend() {
		User storedUser = userStorage.create(user);
		User friend = userStorage.create(otherUser);

		userStorage.addFriend(storedUser.getId(), friend.getId());

		storedUser = userStorage.getById(storedUser.getId());
		assertTrue(storedUser.getFriends().contains(friend.getId()));

		friend = userStorage.getById(friend.getId());
		assertFalse(friend.getFriends().contains(storedUser.getId()));
	}

	@Test
	public void testRemoveFriend() {
		User storedUser = userStorage.create(user);
		User friend = userStorage.create(otherUser);

		userStorage.addFriend(storedUser.getId(), friend.getId());

		storedUser = userStorage.getById(storedUser.getId());
		assertTrue(storedUser.getFriends().contains(friend.getId()));

		userStorage.deleteFriend(storedUser.getId(), friend.getId());

		storedUser = userStorage.getById(storedUser.getId());
		assertTrue(storedUser.getFriends().isEmpty());
	}

	@Test
	public void testGetFriends() {
		User storedUser = userStorage.create(user);
		User friend = userStorage.create(otherUser);

		userStorage.addFriend(storedUser.getId(), friend.getId());
		Collection<User> friends = userStorage.getFriends(storedUser.getId());

		assertEquals(friends.size(), 1);
		assertTrue(friends.contains(friend));
	}

	@Test
	public void testCommonFriends() {
		User storedUser = userStorage.create(user);
		User storedUser2 = userStorage.create(otherUser);
		User user3 = User.builder()
				.email("ersya.ru")
				.name("Borya")
				.login("borers")
				.build();
		User commonFriend = userStorage.create(user3);

		userStorage.addFriend(storedUser.getId(), commonFriend.getId());
		userStorage.addFriend(storedUser2.getId(), commonFriend.getId());

		Collection<User> storedCommonFriends = userStorage.getCommonFriends(storedUser.getId(), storedUser2.getId());
		assertEquals(storedCommonFriends.size(), 1);
		assertTrue(storedCommonFriends.contains(commonFriend));
	}

	@Test
	public void testFindFilmById() {
		Film storedFilm = filmStorage.create(film);

		Film film2 = filmStorage.getById(storedFilm.getId());

		assertNotNull(film2);
		assertEquals(storedFilm.getId(), film2.getId());
		assertEquals(storedFilm, film2);
	}

	@Test
	public void testFindFilmByWrongId() {
		Film film2 = filmStorage.getById(1000);
		assertNull(film2);
	}

	@Test
	public void testFindAllFilms() {
		filmStorage.create(film);
		film.setName("Film 2");
		filmStorage.create(film);

		Collection<Film> films = filmStorage.getAll();
		assertFalse(films.isEmpty());
		assertEquals(films.size(), 2);
	}

	@Test
	public void testUpdateFilm() {
		Film storedFilm = filmStorage.create(film);
		storedFilm.setName("new");
		filmStorage.update(storedFilm);

		Film updatedFilm = filmStorage.getById(storedFilm.getId());
		assertEquals(updatedFilm.getId(), storedFilm.getId());
		assertEquals(updatedFilm.getName(), "new");
	}

	@Test
	public void testAddLikeToFilm() {
		Film storedFilm = filmStorage.create(film);
		User storedUser = userStorage.create(user);

		filmStorage.addLike(storedFilm.getId(), storedUser.getId());

		storedFilm = filmStorage.getById(storedFilm.getId());

		assertEquals(storedFilm.getLikes().size(), 1);
		assertTrue(storedFilm.getLikes().contains(storedUser.getId()));
	}

	@Test
	public void testDeleteLikeFromFilm() {
		Film storedFilm = filmStorage.create(film);
		User storedUser = userStorage.create(user);

		filmStorage.addLike(storedFilm.getId(), storedUser.getId());

		storedFilm = filmStorage.getById(storedFilm.getId());
		assertEquals(storedFilm.getLikes().size(), 1);

		filmStorage.deleteLike(storedFilm.getId(), storedUser.getId());
		storedFilm = filmStorage.getById(storedFilm.getId());
		assertTrue(storedFilm.getLikes().isEmpty());
	}

	@Test
	public void testGetPopularFilms() {
		Film storedFilm = filmStorage.create(film);
		User storedUser = userStorage.create(user);
		User storedUser2 = userStorage.create(otherUser);

		filmStorage.addLike(storedFilm.getId(), storedUser.getId());
		filmStorage.addLike(storedFilm.getId(), storedUser2.getId());

		Film film2 = Film.builder()
				.name("Film 2")
				.duration(120)
				.build();
		Film storedFilm2 = filmStorage.create(film2);

		Collection<Film> films = filmStorage.getPopularFilms(1);
		assertEquals(films.size(), 1);
		storedFilm = films.stream().findFirst().get();
		assertEquals(storedFilm.getLikes().size(), 2);
	}

	@Test
	public void testFindMpaById() {
		Rating storedRating = ratingStorage.getById(1);

		assertNotNull(storedRating);
		assertEquals(storedRating.getId(), 1);
		assertEquals(storedRating.getName(), "G");
	}

	@Test
	public void testFindMpaByWrongId() {
		Rating storedRating = ratingStorage.getById(1000);
		assertNull(storedRating);
	}

	@Test
	public void testFindAllMpa() {
		List<Rating> ratings = ratingStorage.getAll();
		assertFalse(ratings.isEmpty());
		assertEquals(ratings.size(), 5);
	}

	@Test
	public void testFindGenreById() {
		Genre storedGenre = genreStorage.getById(1);

		assertNotNull(storedGenre);
		assertEquals(storedGenre.getId(), 1);
		assertEquals(storedGenre.getName(), "Комедия");
	}

	@Test
	public void testFindGenreByWrongId() {
		Genre storedGenre = genreStorage.getById(1000);
		assertNull(storedGenre);
	}

	@Test
	public void testFindAllGenres() {
		List<Genre> genres = genreStorage.getAll();
		assertFalse(genres.isEmpty());
		assertEquals(genres.size(), 6);
	}
}
