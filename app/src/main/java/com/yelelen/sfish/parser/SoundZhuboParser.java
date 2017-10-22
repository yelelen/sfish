package com.yelelen.sfish.parser;

import com.yelelen.sfish.Model.SoundZhuboModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yelelen on 17-10-21.
 */

public class SoundZhuboParser extends JsonParserImpl<SoundZhuboModel> {
    @Override
    public List<SoundZhuboModel> parse(String json) {
        List<SoundZhuboModel> models = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray hits = jsonObject.getJSONObject("hits").getJSONArray("hits");
            if (hits.length() == 0)
                return null;

            for (int i = 0; i < hits.length(); i++) {
                SoundZhuboModel model = new SoundZhuboModel();
                JSONObject item = hits.getJSONObject(i);
                JSONObject source = item.getJSONObject("_source");
                model.setOrder(source.getInt("az_order"));
                model.setBrief(source.getString("az_brief"));
                model.setCover(source.getString("az_portrait"));
                model.setFansCount(source.getInt("az_fans_count"));
                model.setZanCount(source.getInt("az_love_count"));
                model.setFollowCount(source.getInt("az_follow_count"));
                model.setSoundCount(source.getInt("az_sounds_count"));
                model.setNickname(source.getString("az_nickname"));
                models.add(model);
            }
            return models;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
