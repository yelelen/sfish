package com.yelelen.sfish.Model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by yelelen on 17-10-16.
 */

@Table(database = DB.class)
public class SoundTrackModel {
    @PrimaryKey
    @Column(name = "_id")
    private int order;
    @Column(name = "title")
    private String title;
    @Column(name = "duration")
    private int duration;
    @Column(name = "paths")
    private String paths;

    public SoundTrackModel() {
    }

    public SoundTrackModel(int order, String title, int duration, String paths) {
        this.order = order;
        this.title = title;
        this.duration = duration;
        this.paths = paths;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    @Override
    public String toString() {
        return "SoundTrackModel{" +
                "order=" + order +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", paths=" + paths +
                '}';
    }
}
