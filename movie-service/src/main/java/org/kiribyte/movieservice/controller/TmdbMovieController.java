package org.kiribyte.movieservice.controller;

import org.kiribyte.movieservice.dto.TmdbSearchResponse;
import org.kiribyte.movieservice.dto.tmdb.TmdbSearchResult;
import org.kiribyte.movieservice.service.impl.TmdbService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tmdb")
public class TmdbMovieController {

    public final TmdbService tmdbService;

    public TmdbMovieController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping()
    public TmdbSearchResponse findByTitle(@RequestParam(required = true) String title,
                                          @RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) String year) {
        return tmdbService.searchMovies(title, page, year);
    }
}
