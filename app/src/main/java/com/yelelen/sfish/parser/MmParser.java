package com.yelelen.sfish.parser;

import com.yelelen.sfish.Model.MmItemModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yelelen on 17-9-14.
 */

public class MmParser extends JsonParserImpl<MmItemModel> {

    @Override
    public List<MmItemModel> parse(String json) {
        List<MmItemModel> models = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray hits = jsonObject.getJSONObject("hits").getJSONArray("hits");
            if (hits.length() == 0)
                return null;

            for (int i = 0; i < hits.length(); i++) {
                MmItemModel model = new MmItemModel();
                JSONObject item = hits.getJSONObject(i);
                JSONObject source = item.getJSONObject("_source");
                model.setOrder(source.getInt("mm_order"));
                model.setSeenNum(source.getInt("mm_seen_num"));
                model.setTitle(source.getString("mm_title"));
                model.setFavNum(source.getInt("mm_fav_num"));
                model.setTotalNum(source.getInt("mm_total_num"));
                model.setTag(source.getString("mm_tags"));
                model.setUrl(source.getString("mm_first_image_url"));
                models.add(model);
            }
            return models;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}
