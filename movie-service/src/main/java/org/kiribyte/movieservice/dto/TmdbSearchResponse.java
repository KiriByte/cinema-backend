package org.kiribyte.movieservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TmdbSearchResponse {
    private Integer page;
    private Integer totalPages;
    private Integer totalResults;
    private List<TmdbMovieResponse> movies =  new ArrayList<>();
}
