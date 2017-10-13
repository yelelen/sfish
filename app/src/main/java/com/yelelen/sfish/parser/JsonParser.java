package com.yelelen.sfish.parser;

import java.util.List;

/**
 * Created by yelelen on 17-9-14.
 */

public interface JsonParser<T> {
    List<T> parse(String json);
    T parseOne(String json);
}
