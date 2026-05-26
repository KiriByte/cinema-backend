package org.kiribyte.movieservice.dto.tmdb;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExternalTmdbMovie {
    public Boolean adult;
    public String backdrop_path;
    public ArrayList<Integer> genre_ids =  new ArrayList<>();
    public Integer id;
    public String original_language;
    public String original_title;
    public String overview;
    public Double popularity;
    public String poster_path;
    public String release_date;
    public String title;
    public Boolean video;
    public Double vote_average;
    public Integer vote_count;
    //----------------------------
    public Integer runtime;
}
