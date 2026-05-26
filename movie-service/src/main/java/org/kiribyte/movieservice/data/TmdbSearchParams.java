package org.kiribyte.movieservice.data;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TmdbSearchParams {
    public String query;
    public Boolean include_adult;
    public String language;
    public String primary_release_year;
    public Integer page;
    public String region;
    public String year;

    public Map<String, Object> toQueryMap() {
        Map<String, Object> map = new HashMap<>();
        if (query != null) map.put("query", query);
        if (include_adult != null) map.put("include_adult", include_adult);
        if (language != null) map.put("language", language);
        if (primary_release_year != null) map.put("primary_release_year", primary_release_year);
        if (page != null) map.put("page", page);
        if (region != null) map.put("region", region);
        if (year != null) map.put("year", year);
        return map;
    }
}
