package org.kiribyte.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {
    private UUID id;
    private String title;
    private String description;
    private Integer duration;
    private String poster_url;
}

