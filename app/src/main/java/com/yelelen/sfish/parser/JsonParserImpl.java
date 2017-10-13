package com.yelelen.sfish.parser;

import java.util.List;

/**
 * Created by yelelen on 17-9-15.
 */

public abstract class JsonParserImpl<T> implements JsonParser<T>{
    @Override
    public List<T> parse(String json) {
        return null;
    }

    @Override
    public T parseOne(String json) {
        return null;
    }
}
