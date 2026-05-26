package org.kiribyte.movieservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TmdbMovieResponse {
    public Integer id;
    public String title;
    public String original_title;
    public String overview;
    public String release_date;
    public String poster_path;
}
