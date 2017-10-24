package com.yelelen.sfish.parser;

import com.yelelen.sfish.Model.SoundTrackModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yelelen on 17-10-21.
 */

public class SoundTrackParser extends JsonParserImpl<SoundTrackModel> {
    @Override
    public List<SoundTrackModel> parse(String json) {
        List<SoundTrackModel> models = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray hits = jsonObject.getJSONObject("hits").getJSONArray("hits");
            if (hits.length() == 0)
                return null;

            for (int i = 0; i < hits.length(); i++) {
                SoundTrackModel model = new SoundTrackModel();
                JSONObject item = hits.getJSONObject(i);
                JSONObject source = item.getJSONObject("_source");
                model.setOrder(source.getInt("as_order"));
                model.setTitle(source.getString("as_title"));
                model.setDuration(source.getInt("as_duration"));
                model.setPlayCount(source.getInt("as_play_count"));
                model.setFavCount(source.getInt("as_favorites_count"));
                model.setPaths(source.getString("as_play_paths"));
                model.setAlbumId(source.getInt("as_album_id"));
                models.add(model);
            }
            return models;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
