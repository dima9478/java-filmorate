package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/films", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film addFilm(@RequestBody Film film) {
        Film createdFilm = filmService.add(film);

        log.debug("Film {} was created", createdFilm);
        return createdFilm;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Film putFilm(@RequestBody Film film) {
        Film updatedFilm = filmService.update(film);

        log.debug("Film {} was updated: {}", film.getId(), updatedFilm);
        return updatedFilm;
    }
}
