package com.yelelen.sfish.Model;

/**
 * Created by yelelen on 17-10-13.
 */

public class SoundRecyclerModel {
    private String category;
    private String category1;
    private String category2;
    private String category3;

    public SoundRecyclerModel(String category, String category1, String category2, String category3) {
        this.category = category;
        this.category1 = category1;
        this.category2 = category2;
        this.category3 = category3;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getCategory3() {
        return category3;
    }

    public void setCategory3(String category3) {
        this.category3 = category3;
    }
}
