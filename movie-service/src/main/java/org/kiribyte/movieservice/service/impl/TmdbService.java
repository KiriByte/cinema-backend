package org.kiribyte.movieservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.kiribyte.movieservice.client.TmdbClient;
import org.kiribyte.movieservice.data.TmdbSearchParams;
import org.kiribyte.movieservice.dto.tmdb.ExternalTmdbMovie;
import org.kiribyte.movieservice.dto.tmdb.ExternalTmdbSearchResult;
import org.kiribyte.movieservice.service.ImageStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Service
public class TmdbService {

    private final static String TMDBIMAGEURL = "https://image.tmdb.org/t/p/original";

    private final TmdbClient tmdbClient;
    private final ImageStorageService posterStorageService;

    public TmdbService(TmdbClient tmdbClient, ImageStorageService posterStorageService) {
        this.tmdbClient = tmdbClient;
        this.posterStorageService = posterStorageService;
    }

    public ExternalTmdbSearchResult searchMovies(String query, Integer page, String year) {
        TmdbSearchParams searchParams = new TmdbSearchParams();
        searchParams.setQuery(query);
        searchParams.setPage(page);
        searchParams.setYear(year);
        searchParams.setLanguage("ru-RU");
        searchParams.setInclude_adult(true);
        return tmdbClient.searchMovie(searchParams);
    }

    public ExternalTmdbMovie getMovieById(@PathVariable Integer movieId) {
        return tmdbClient.getMovieById(movieId, "ru-RU");
    }


}
