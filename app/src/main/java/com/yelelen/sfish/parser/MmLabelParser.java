package com.yelelen.sfish.parser;

import com.yelelen.sfish.Model.MmLabelModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yelelen on 17-10-6.
 */

public class MmLabelParser extends JsonParserImpl<MmLabelModel> {
    @Override
    public List<MmLabelModel> parse(String json) {
        List<MmLabelModel> models = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray hits = jsonObject.getJSONObject("hits").getJSONArray("hits");
            if (hits.length() == 0)
                return null;

            for (int i = 0; i < hits.length(); i++) {
                MmLabelModel model = new MmLabelModel();
                JSONObject item = hits.getJSONObject(i);
                JSONObject source = item.getJSONObject("_source");
                model.setOrder(source.getInt("order"));
                model.setCover(source.getString("cover"));
                model.setLabel(source.getString("label"));
                models.add(model);
            }
            return models;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
