package com.yelelen.sfish.parser;

import com.yelelen.sfish.Model.SoundItemModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yelelen on 17-10-13.
 */

public class SoundParser extends JsonParserImpl<SoundItemModel> {

    @Override
    public List<SoundItemModel> parse(String json) {
        List<SoundItemModel> models = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray hits = jsonObject.getJSONObject("hits").getJSONArray("hits");
            if (hits.length() == 0)
                return null;

            for (int i = 0; i < hits.length(); i++) {
                SoundItemModel model = new SoundItemModel();
                JSONObject item = hits.getJSONObject(i);
                JSONObject source = item.getJSONObject("_source");
                model.setOrder(source.getInt("aa_order"));
                model.setZhuboId(source.getInt("aa_zhubo_id"));
                model.setTitle(source.getString("aa_title"));
                model.setCover(source.getString("aa_cover"));
                model.setTag(source.getString("aa_tag"));
                model.setLastUpdateTime(source.getString("aa_last_update"));
                model.setPlayCount(source.getString("aa_play_count"));
                model.setDesc(source.getString("aa_desc"));
                model.setSounds(source.getString("aa_sounds"));
                models.add(model);
            }
            return models;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
