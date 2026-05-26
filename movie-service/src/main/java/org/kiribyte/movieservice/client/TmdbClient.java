package org.kiribyte.movieservice.client;

import org.kiribyte.movieservice.config.FeignConfigTmdb;
import org.kiribyte.movieservice.data.TmdbSearchParams;
import org.kiribyte.movieservice.dto.tmdb.ExternalTmdbMovie;
import org.kiribyte.movieservice.dto.tmdb.ExternalTmdbSearchResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "TmdbMovie", url = "https://api.themoviedb.org/3", configuration = FeignConfigTmdb.class)
public interface TmdbClient {

    @RequestMapping(method = RequestMethod.GET, value = "/search/movie")
    ExternalTmdbSearchResult searchMovie(@SpringQueryMap TmdbSearchParams searchParams);

    @RequestMapping(method = RequestMethod.GET, value = "/movie/{movieId}")
    ExternalTmdbMovie getMovieById(@PathVariable Integer movieId,
                                   @RequestParam(value = "language", defaultValue = "en-EN") String language);
}
