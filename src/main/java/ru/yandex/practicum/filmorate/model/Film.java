package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
@Builder
public class Film {
    private int id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private final Set<Integer> likes = new HashSet<>();
    private final Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
    private Rating mpa;

    public boolean addLike(Integer id) {
        return likes.add(id);
    }

    public boolean removeLike(Integer id) {
        return likes.remove(id);
    }

    public boolean addGenre(Genre genre) {
        return genres.add(genre);
    }

    public boolean removeGenre(Genre genre) {
        return genres.remove(genre);
    }
}
