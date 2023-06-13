package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RequiredArgsConstructor
@RequestMapping(value = "/films", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") int filmId) {
        return filmService.getFilmById(filmId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film addFilm(@RequestBody Film film) {
        return filmService.add(film);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film putFilm(@RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable("id") int filmId,
                        @PathVariable("userId") int userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable("id") int filmId,
                           @PathVariable("userId") int userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(name = "count", defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}
