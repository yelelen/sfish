package com.yelelen.sfish.Model;

/**
 * Created by yelelen on 17-10-13.
 */

public class SoundAlbumItemModel {
    private int order;
    private String title;
    private String cover;
    private String playCount;

    public SoundAlbumItemModel(int order, String title, String cover, String playCount) {
        this.order = order;
        this.title = title;
        this.cover = cover;
        this.playCount = playCount;
    }

    public SoundAlbumItemModel(SoundItemModel model) {
        this.order = model.getOrder();
        this.title = model.getTitle();
        this.cover = model.getCover();
        this.playCount = model.getPlayCount();
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String play_count) {
        this.playCount = play_count;
    }
}
