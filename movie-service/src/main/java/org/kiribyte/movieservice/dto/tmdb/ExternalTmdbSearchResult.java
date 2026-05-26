package org.kiribyte.movieservice.dto.tmdb;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExternalTmdbSearchResult {
    public Integer page;
    public ArrayList<ExternalTmdbMovie> results =  new ArrayList<>();
    public Integer total_pages;
    public Integer total_results;
}
