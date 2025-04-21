package com.BayWave;

import java.net.URLDecoder;
import java.util.Map;
import java.util.stream.Collectors;

public class ParseQuery {

    /**
     * parses query
     * @param query
     * @return
     */
    static Map<String, String> parseQuery(String query) {
        if (query == null) return Map.of();
        return java.util.Arrays.stream(query.split("&"))
                .map(p -> p.split("=", 2))
                .collect(Collectors.toMap(
                        p -> URLDecoder.decode(p[0], java.nio.charset.StandardCharsets.UTF_8),
                        p -> URLDecoder.decode(p[1], java.nio.charset.StandardCharsets.UTF_8)
                ));
    }
}
